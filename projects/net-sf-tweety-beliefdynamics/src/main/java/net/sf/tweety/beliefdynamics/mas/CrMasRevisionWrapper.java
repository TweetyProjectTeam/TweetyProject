package net.sf.tweety.beliefdynamics.mas;

import java.util.*;

import net.sf.tweety.beliefdynamics.*;
import net.sf.tweety.commons.*;

/**
 * This class is a wrapper for a revision for formulas of type T that is
 * used to revise information objects. On revision, the information of the sources is ignored
 * <br>
 * For example: If {A1:F1,...,AN:FN} * {B1:G1,...,BM:GM} is issued to this class then
 * the result is {C1:H1,...,CK:HK} given that {F1,...,FN} *' {G1,...,GM} = {H1,...,HK} with
 * *' being the wrapped revision.  
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of the formulas used by this operator.
 */
public class CrMasRevisionWrapper<T extends Formula> extends MultipleBaseRevisionOperator<InformationObject<T>>{

	/**
	 * The wrapped revision operator.
	 */
	private MultipleBaseRevisionOperator<T> wrappedRevision;
	
	/** Creates a new revision with the given wrapped revision.
	 * @param wrappedRevision some revision
	 */
	public CrMasRevisionWrapper(MultipleBaseRevisionOperator<T> wrappedRevision){
		this.wrappedRevision = wrappedRevision;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.beliefdynamics.MultipleBaseRevisionOperator#revise(java.util.Collection, java.util.Collection)
	 */
	@Override
	public Collection<InformationObject<T>> revise(Collection<InformationObject<T>> base, Collection<InformationObject<T>> formulas) {
		// project on formulas
		Collection<T> projBase = new HashSet<T>();
		for(InformationObject<T> elem: base)
			projBase.add(elem.getFormula());
		Collection<T> projFormulas = new HashSet<T>();
		for(InformationObject<T> elem: formulas)
			projFormulas.add(elem.getFormula());
		// do revision
		Collection<T> projRevision = this.wrappedRevision.revise(projBase, projFormulas);
		// undo projection
		Collection<InformationObject<T>> revision = new HashSet<InformationObject<T>>();
		for(T f: projRevision){
			// add each inf object where f appears
			for(InformationObject<T> elem: base)
				if(elem.getFormula().equals(f)) revision.add(elem);
			for(InformationObject<T> elem: formulas)
				if(elem.getFormula().equals(f)) revision.add(elem);
		}			
		return revision;
	}



}
