/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.bfh.unicrypt.math.product.abstracts;

import ch.bfh.unicrypt.math.concatenative.classes.ByteArrayMonoid;
import ch.bfh.unicrypt.math.concatenative.interfaces.ByteArrayElement;
import ch.bfh.unicrypt.math.general.abstracts.AbstractElement;
import ch.bfh.unicrypt.math.general.interfaces.Element;
import ch.bfh.unicrypt.math.product.interfaces.Tuple;
import ch.bfh.unicrypt.math.utility.Compound;
import ch.bfh.unicrypt.math.utility.MathUtil;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author rolfhaenni
 */
public abstract class AbstractTuple<S extends AbstractProductSet, T extends Tuple, E extends Element> extends AbstractElement<S, T> implements Tuple {

  private final E[] elements;
  private final int arity;

  protected AbstractTuple(final S set, final E[] elements) {
    super(set);
    this.elements = elements;
    this.arity = elements.length;
  }

  @Override
  protected BigInteger standardGetValue() {
    int arity = this.getArity();
    BigInteger[] values = new BigInteger[arity];
    for (int i = 0; i < arity; i++) {
      values[i] = this.elements[i].getValue();
    }
    return MathUtil.elegantPair(values);
  }

  @Override
  protected ByteArrayElement standardGetRecursiveHashValue(MessageDigest messageDigest) {
    int arity = this.getArity();
    ByteArrayElement[] hashValues = new ByteArrayElement[arity];
    for (int i = 0; i < arity; i++) {
      hashValues[i] = this.getAt(i).getRecursiveHashValue(messageDigest);
    }
    return ByteArrayMonoid.getInstance().apply(hashValues).getHashValue(messageDigest);
  }

  @Override
  public int getArity() {
    return this.arity;
  }

  @Override
  public final boolean isNull() {
    return this.getArity() == 0;
  }

  @Override
  public final boolean isUniform() {
    return this.elements.length <= 1;
  }

  @Override
  public E getFirst() {
    return this.getAt(0);

  }

  @Override
  public E getAt(int index) {
    if (index < 0 || index >= this.getArity()) {
      throw new IndexOutOfBoundsException();
    }
    if (this.isUniform()) {
      return this.elements[0];
    }
    return this.elements[index];
  }

  @Override
  public E getAt(int... indices) {
    if (indices == null) {
      throw new IllegalArgumentException();
    }
    E element = (E) this;
    for (final int index : indices) {
      if (element.isCompound()) {
        element = (E) ((T) element).getAt(index);
      } else {
        throw new IllegalArgumentException();
      }
    }
    return element;
  }

  @Override
  public E[] getAll() {
    int arity = this.getArity();
    E[] result = (E[]) new Element[arity];
    for (int i = 0; i < arity; i++) {
      result[i] = this.getAt(i);
    }
    return result;
  }

  @Override
  public Iterator<Element> iterator() {
    final Compound<Element> tuple = this;
    return new Iterator<Element>() {
      int currentIndex = 0;

      @Override
      public boolean hasNext() {
        return currentIndex < tuple.getArity();
      }

      @Override
      public E next() {
        if (this.hasNext()) {
          return (E) tuple.getAt(this.currentIndex++);
        }
        throw new NoSuchElementException();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
      }
    };
  }

  @Override
  protected boolean standardEquals(Element element) {
    T other = (T) element;
    for (int i = 0; i < this.getArity(); i++) {
      if (!this.getAt(i).equals(other.getAt(i))) {
        return false;
      }
    }
    return true;
  }

  @Override
  protected boolean standardIsCompound() {
    return true;
  }

  @Override
  protected int standardHashCode() {
    final int prime = 31;
    int result = 1;
    for (Element element : this) {
      result = prime * result + element.hashCode();
    }
    return result;
  }

  @Override
  protected String standardToString() {
    String result = "";
    String separator = "";
    for (Element element : this) {
      result = result + separator + element.toString();
      separator = ",";
    }
    return result;
  }

}
