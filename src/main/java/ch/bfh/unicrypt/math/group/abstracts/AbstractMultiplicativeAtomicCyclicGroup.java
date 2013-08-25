package ch.bfh.unicrypt.math.group.abstracts;

import ch.bfh.unicrypt.math.element.classes.MultiplicativeAtomicElement;
import java.math.BigInteger;

import ch.bfh.unicrypt.math.element.interfaces.Element;
import ch.bfh.unicrypt.math.group.interfaces.MultiplicativeCyclicGroup;

public abstract class AbstractMultiplicativeAtomicCyclicGroup extends AbstractAtomicCyclicGroup<MultiplicativeAtomicElement> implements MultiplicativeCyclicGroup {

  private static final long serialVersionUID = 1L;

  @Override
  protected MultiplicativeAtomicElement abstractGetElement(BigInteger value) {
    return new MultiplicativeAtomicElement(this, value) {};
  }

  @Override
  public final MultiplicativeAtomicElement multiply(final Element element1, final Element element2) {
    return this.apply(element1, element2);
  }

  @Override
  public final MultiplicativeAtomicElement multiply(final Element... elements) {
    return this.apply(elements);
  }

  @Override
  public final MultiplicativeAtomicElement power(final Element element, final BigInteger amount) {
    return this.selfApply(element, amount);
  }

  @Override
  public final MultiplicativeAtomicElement power(final Element element, final Element amount) {
    return this.selfApply(element, amount);
  }

  @Override
  public final MultiplicativeAtomicElement power(final Element element, final int amount) {
    return this.selfApply(element, amount);
  }

  @Override
  public final MultiplicativeAtomicElement square(Element element) {
    return this.selfApply(element);
  }

  @Override
  public final MultiplicativeAtomicElement productOfPowers(Element[] elements, BigInteger[] amounts) {
    return this.multiSelfApply(elements, amounts);
  }

  @Override
  public final MultiplicativeAtomicElement divide(final Element element1, final Element element2) {
    return this.applyInverse(element1, element2);
  }

}