package edu.cmu.cs.sasylf.ast;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cmu.cs.sasylf.util.ErrorHandler;
import edu.cmu.cs.sasylf.util.Location;
import edu.cmu.cs.sasylf.util.Span;

public class AndJudgment extends AndOrJudgment {
	public static class AndTerminal extends Terminal {
		public AndTerminal(Span loc) {
			super("'and'",loc);
		}

		@Override
		public void prettyPrint(PrintWriter out, PrintContext ctx) {
			out.print("and");
		}

	}

	private static Terminal makeAndTerminal(Span loc) {
		return new AndTerminal(loc);
	}

	@Override
	public Terminal makeSeparator(Span l) {
		return makeAndTerminal(l);
	}

	public static void addAnd(Clause cl, Span and, Clause more) {
		cl.getElements().add(new AndTerminal(and));
		cl.getElements().addAll(more.getElements());
	}

	private AndJudgment(Location l, List<Judgment> parts) {
		super(null,makeName(parts),new ArrayList<Rule>(),makeForm(l,parts),findAssume(l,parts));
		String name = super.getName();
		this.parts = parts;
		int u = 0;
		List<Clause> premises = new ArrayList<Clause>();
		List<Element> concElems = new ArrayList<Element>();
		for (Judgment j : parts) {
			if (!premises.isEmpty()) {
				concElems.add(makeAndTerminal(l));
			}
			List<Element> es = new ArrayList<Element>();
			NonTerminal root = j.getAssume();
			for (Element e : j.getForm().getElements()) {
				if (e instanceof NonTerminal && !e.equals(root)) {
					SyntaxDeclaration s = ((NonTerminal)e).getType();
					NonTerminal gen = new NonTerminal(s.toString()+ ++u,l);
					gen.setType(s);
					es.add(gen);
					concElems.add(gen);
				} else {
					es.add(e);
					concElems.add(e);
				}
			}
			premises.add(new ClauseUse(l,es,(ClauseDef)j.getForm()));
		}
		ClauseDef cd = new ClauseDef(super.getForm(), this, super.getName());
		super.setForm(cd);
		Clause result = new ClauseUse(l,concElems,cd);
		super.getRules().add(new Rule(l,name,premises,result));
	}

	/**
	 * Create the name for an "and" judgment for 
	 * @param parts
	 * @return
	 */
	 private static String makeName(List<Judgment> parts) {
		 StringBuilder sb = new StringBuilder();
		 sb.append("and[");
		 boolean first = true;
		 for (Judgment j : parts) {
			 if (first) first = false; else sb.append(',');
			 sb.append(j.getName());
		 }
		 sb.append(']');
		 return sb.toString();
	 }

	 private static Clause makeForm(Location l, List<Judgment> parts) {
		 Clause result = new Clause(l);
		 boolean started = false;
		 NonTerminal context = null;
		 for (Judgment j : parts) {
			 if (started) result.getElements().add(makeAndTerminal(l));
			 else started = true;
			 for (Element e : j.getForm().getElements()) {
				 if (e instanceof NonTerminal && ((NonTerminal)e).getType().isInContextForm()) {
					 if (context == null) {
						 context = (NonTerminal)e;
					 } else {
						 if (!context.equals(e)) {
							 ErrorHandler.report("All contexts in an 'and' judgment must be the same", l);
						 }
					 }
				 }
				 result.getElements().add(e);
			 }
		 }
		 return result;
	 }

	 private static NonTerminal findAssume(Location loc, List<Judgment> parts) {
		 NonTerminal result = null;
		 for (Judgment j : parts) {
			 NonTerminal a = j.getAssume();
			 if (a != null) {
				 if (result == null) result = a;
				 else if (!result.equals(a)) {
					 ErrorHandler.report("cannot conjoin judgments with different assumptions", loc);
				 }
			 }
		 }
		 return result;
	 }

	 private static Map<List<Judgment>,AndJudgment> cache = new HashMap<List<Judgment>,AndJudgment>();

	 /**
	  * Generate an "and" judgment for conjoining a series of judgments together.
	  * @param loc location where generation was (first) required
	  * @param parts list of judgments 
	  * @return judgment that is the conjunction of the parts
	  */
	 public static AndJudgment makeAndJudgment(Location loc, Context ctx, List <Judgment> parts) {
		 AndJudgment result = cache.get(parts);
		 if (result == null) {
			 parts = new ArrayList<Judgment>(parts); // defensive programming
			 result = new AndJudgment(loc,parts);
			 result.defineConstructor(ctx);
			 result.typecheck(ctx);
			 cache.put(parts,result);
		 }
		 return result;
	 }

	 public static AndJudgment makeEmptyAndJudgment(Location loc) {
		 List<Judgment> empty = Collections.<Judgment>emptyList();
		 AndJudgment result = cache.get(empty);
		 if (result == null) {
			 result = new AndJudgment(loc,empty);
			 cache.put(empty, result);
		 }
		 return result;
	 }

}
