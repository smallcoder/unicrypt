package ch.bfh.unicrypt.crypto.schemes.sharing.abstracts;

import ch.bfh.unicrypt.crypto.schemes.scheme.abstracts.AbstractScheme;
import ch.bfh.unicrypt.crypto.schemes.sharing.interfaces.SecretSharingScheme;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Set;
import java.util.Random;

public abstract class AbstractSecretSharingScheme<MS extends Set, ME extends Element, SS extends Set, SE extends Element>
			 extends AbstractScheme<MS>
			 implements SecretSharingScheme {

	private final int size;

	protected AbstractSecretSharingScheme(int size) {
		this.size = size;
	}

	@Override
	public final SS getShareSpace() {
		return this.abstractGetShareSpace();
	}

	@Override
	public final int getSize() {
		return this.size;
	}

	@Override
	public final SE[] share(Element message) {
		return this.share(message, (Random) null);
	}

	@Override
	public final SE[] share(Element message, Random random) {
		if (message == null || !this.getMessageSpace().contains(message)) {
			throw new IllegalArgumentException();
		}
		return this.abstractShare(message, random);
	}

	@Override
	public final ME recover(Element... shares) {
		if (shares == null || shares.length < this.getThreshold() || shares.length > this.getSize()) {
			throw new IllegalArgumentException();
		}
		for (Element share : shares) {
			if (share == null || !this.getShareSpace().contains(share)) {
				throw new IllegalArgumentException();
			}
		}
		return this.abstractRecover(shares);
	}

	protected int getThreshold() { // this method is not really needed here, but it simplifies the method recover
		return this.getSize();
	}

	protected abstract SS abstractGetShareSpace();

	protected abstract SE[] abstractShare(Element message, Random random);

	protected abstract ME abstractRecover(Element[] shares);

}