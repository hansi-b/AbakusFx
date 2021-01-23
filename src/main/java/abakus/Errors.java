package abakus;

class Errors {

	static IllegalArgumentException illegalArg(final String fmt, final Object... args) {
		return new IllegalArgumentException(String.format(fmt, args));
	}

}