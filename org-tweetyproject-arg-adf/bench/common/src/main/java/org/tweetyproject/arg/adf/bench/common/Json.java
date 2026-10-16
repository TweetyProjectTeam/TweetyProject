package org.tweetyproject.arg.adf.bench.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal JSON support for the one-line runner protocol and the JSONL result
 * files: objects, arrays, strings, numbers, booleans and null.
 */
public final class Json {

	private Json() {}

	/** @return the JSON text of a value built from maps, lists, strings, numbers, booleans and null */
	public static String write(Object value) {
		StringBuilder s = new StringBuilder();
		write(value, s);
		return s.toString();
	}

	private static void write(Object value, StringBuilder s) {
		if (value == null) {
			s.append("null");
		} else if (value instanceof String str) {
			s.append('"');
			for (char c : str.toCharArray()) {
				switch (c) {
					case '"' -> s.append("\\\"");
					case '\\' -> s.append("\\\\");
					case '\n' -> s.append("\\n");
					case '\r' -> s.append("\\r");
					case '\t' -> s.append("\\t");
					default -> {
						if (c < 0x20) {
							s.append(String.format("\\u%04x", (int) c));
						} else {
							s.append(c);
						}
					}
				}
			}
			s.append('"');
		} else if (value instanceof Number || value instanceof Boolean) {
			s.append(value);
		} else if (value instanceof Map<?, ?> map) {
			s.append('{');
			Iterator<? extends Map.Entry<?, ?>> it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<?, ?> e = it.next();
				write(String.valueOf(e.getKey()), s);
				s.append(':');
				write(e.getValue(), s);
				if (it.hasNext()) {
					s.append(',');
				}
			}
			s.append('}');
		} else if (value instanceof Iterable<?> list) {
			s.append('[');
			Iterator<?> it = list.iterator();
			while (it.hasNext()) {
				write(it.next(), s);
				if (it.hasNext()) {
					s.append(',');
				}
			}
			s.append(']');
		} else {
			throw new IllegalArgumentException("Not a JSON value: " + value.getClass());
		}
	}

	/** @return the parsed value: Map, List, String, Double/Long, Boolean or null */
	public static Object parse(String text) {
		Parser p = new Parser(text);
		Object value = p.value();
		p.skipWhitespace();
		if (p.pos != text.length()) {
			throw new IllegalArgumentException("Trailing characters in JSON: " + text);
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> parseObject(String text) {
		return (Map<String, Object>) parse(text);
	}

	private static final class Parser {
		final String s;
		int pos;

		Parser(String s) {
			this.s = s;
		}

		void skipWhitespace() {
			while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
				pos++;
			}
		}

		Object value() {
			skipWhitespace();
			char c = s.charAt(pos);
			if (c == '{') {
				pos++;
				Map<String, Object> map = new LinkedHashMap<>();
				skipWhitespace();
				if (s.charAt(pos) == '}') {
					pos++;
					return map;
				}
				while (true) {
					skipWhitespace();
					String key = (String) value();
					skipWhitespace();
					expect(':');
					map.put(key, value());
					skipWhitespace();
					if (s.charAt(pos) == ',') {
						pos++;
					} else {
						expect('}');
						return map;
					}
				}
			} else if (c == '[') {
				pos++;
				List<Object> list = new ArrayList<>();
				skipWhitespace();
				if (s.charAt(pos) == ']') {
					pos++;
					return list;
				}
				while (true) {
					list.add(value());
					skipWhitespace();
					if (s.charAt(pos) == ',') {
						pos++;
					} else {
						expect(']');
						return list;
					}
				}
			} else if (c == '"') {
				pos++;
				StringBuilder b = new StringBuilder();
				while (s.charAt(pos) != '"') {
					char d = s.charAt(pos++);
					if (d == '\\') {
						char e = s.charAt(pos++);
						switch (e) {
							case 'n' -> b.append('\n');
							case 'r' -> b.append('\r');
							case 't' -> b.append('\t');
							case 'u' -> {
								b.append((char) Integer.parseInt(s.substring(pos, pos + 4), 16));
								pos += 4;
							}
							default -> b.append(e);
						}
					} else {
						b.append(d);
					}
				}
				pos++;
				return b.toString();
			} else if (s.startsWith("true", pos)) {
				pos += 4;
				return Boolean.TRUE;
			} else if (s.startsWith("false", pos)) {
				pos += 5;
				return Boolean.FALSE;
			} else if (s.startsWith("null", pos)) {
				pos += 4;
				return null;
			}
			int start = pos;
			while (pos < s.length() && "+-0123456789.eE".indexOf(s.charAt(pos)) >= 0) {
				pos++;
			}
			String number = s.substring(start, pos);
			if (number.isEmpty()) {
				throw new IllegalArgumentException("Invalid JSON at " + start + ": " + s);
			}
			return number.matches("-?\\d+") ? (Object) Long.parseLong(number) : (Object) Double.parseDouble(number);
		}

		void expect(char c) {
			if (s.charAt(pos) != c) {
				throw new IllegalArgumentException("Expected '" + c + "' at " + pos + ": " + s);
			}
			pos++;
		}
	}

}
