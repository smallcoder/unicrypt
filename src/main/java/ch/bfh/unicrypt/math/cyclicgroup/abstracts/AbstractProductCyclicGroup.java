/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.bfh.unicrypt.math.cyclicgroup.abstracts;

import ch.bfh.unicrypt.math.cyclicgroup.classes.ProductCyclicGroup;
import ch.bfh.unicrypt.math.cyclicgroup.interfaces.CyclicGroup;
import ch.bfh.unicrypt.math.element.interfaces.Element;
import ch.bfh.unicrypt.math.element.interfaces.Tuple;
import ch.bfh.unicrypt.math.group.abstracts.AbstractProductGroup;
import ch.bfh.unicrypt.math.group.classes.ProductGroup;
import ch.bfh.unicrypt.math.group.interfaces.Group;
import java.util.Random;

/**
 *
 * @author rolfhaenni
 */
public abstract class AbstractProductCyclicGroup<S extends CyclicGroup> extends AbstractProductGroup<S> implements CyclicGroup {

  private Tuple defaultGenerator;

  protected AbstractProductCyclicGroup(final CyclicGroup[] cyclicGroups) {
    super(cyclicGroups);
  }

  protected AbstractProductCyclicGroup(final CyclicGroup cyclicGroup, final int arity) {
    super(cyclicGroup, arity);
  }

  protected AbstractProductCyclicGroup() {
    super();
  }

  @Override
  public final Tuple getDefaultGenerator() {
    if (this.defaultGenerator == null) {
      Element[] generators = new Element[this.getArity()];
      for (int i=0; i<this.getArity(); i++) {
        generators[i] = this.getAt(i).getDefaultGenerator();
      }
      this.defaultGenerator = this.standardGetElement(generators);
    }
    return this.defaultGenerator;
  }

  @Override
  public final Tuple getRandomGenerator() {
    return this.getRandomGenerator(null);
  }

  @Override
  public final Tuple getRandomGenerator(Random random) {
    int arity = this.getArity();
    Element[] randomElements = new Element[arity];
    for (int i=0; i<arity; i++) {
      randomElements[i] = this.getAt(i).getRandomElement(random);
    }
    return this.standardGetElement(randomElements);
  }

  @Override
  public final boolean isGenerator(Element element) {
    if (!this.contains(element)) {
      throw new IllegalArgumentException();
    }
    Tuple compoundElement = (Tuple) element;
    for (int i=0; i<this.getArity(); i++) {
      if (!this.getAt(i).isGenerator(compoundElement.getAt(i))) {
        return false;
      }
    }
    return true;
  }

}
