/**
 *
 */
package ru.myx.ae1.storage;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.myx.ae3.Engine;

final class NameGenerator {

	private final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmm");

	private final int[] check = new int[9000];

	@Override
	public String toString() {

		final Date date = Engine.CURRENT_TIME;
		for (int i = 9; i >= 0; --i) {
			final int compare = (int) (date.getTime() / 60000L);
			final int rand = Engine.createRandom(9000);
			if (this.check[rand] == compare) {
				continue;
			}
			this.check[rand] = compare;
			return this.SDF.format(date) + '-' + (rand + 1000) + ".htm";
		}
		return Engine.createGuid() + ".htm";
	}
}
