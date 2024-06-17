package me.ishyro.core.api.sql;

public enum DataType {

	INTEGER,
	TEXT,
	VARCHAR36,
	VARCHAR16,
	REAL;

	public String toString() {
		switch (this) {
			case INTEGER:
				return "INTERGER";
			case TEXT:
				return "TEXT";
			case VARCHAR36:
				return "VARCHAR(36)";
			case VARCHAR16:
				return "VARCHAR(16)";
			case REAL:
				return "REAL";
			default:
				return "NULL";
		}
	}
}