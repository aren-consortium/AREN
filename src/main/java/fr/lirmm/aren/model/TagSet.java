package fr.lirmm.aren.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;

/**
 *
 * @author florent
 */
public class TagSet extends HashSet<TagSet.Tag> {

    /**
     *
     */
    public TagSet() {
        super();
    }

    /**
     *
     * @param serial
     */
    public TagSet(String serial) {
        this();
        for (String tagSerial : serial.split("\\*")) {
            if (tagSerial.length() > 0) {
                this.add(new Tag(tagSerial));
            }
        }
    }

    /**
     *
     * @param tag
     * @return
     */
    @Override
    public boolean add(Tag tag) {
        this.remove(tag);
        return super.add(tag);
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        java.util.List<String> tagString = this.stream().map((Tag tag) -> {
            return tag.toString();
        }).collect(Collectors.toList());
        return String.join("*", tagString);
    }

    /**
     *
     */
    @javax.persistence.Converter(autoApply = true)
    public static class Converter implements AttributeConverter<TagSet, String> {

        /**
         *
         * @param x
         * @return
         */
        @Override
        public String convertToDatabaseColumn(TagSet x) {
            if (x == null) {
                return null;
            }
            return x.toString();
        }

        /**
         *
         * @param y
         * @return
         */
        @Override
        public TagSet convertToEntityAttribute(String y) {
            if (y == null) {
                return null;
            }
            return new TagSet(y);
        }
    }

    /**
     *
     */
    public static class Tag implements Serializable {

        private String value;
        private boolean negative = false;
        private Float power = 0f;

        /**
         *
         */
        public Tag() {
        }

        /**
         *
         * @param serial
         */
        public Tag(String serial) {
            String[] split = serial.split("\\|");
            if (split.length == 2) {
                split[1] = split[1].trim();
                this.power = Float.parseFloat(split[1]);
            }
            split[0] = split[0].trim();
            if (split[0].startsWith("-")) {
                this.negative = true;
                this.value = split[0].substring(1).trim();
            } else {
                this.negative = false;
                this.value = split[0];
            }
        }

        /**
         *
         * @return
         */
        public String getValue() {
            return value;
        }

        /**
         *
         * @param value
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         *
         * @return
         */
        public boolean isNegative() {
            return negative;
        }

        /**
         *
         * @param negative
         */
        public void setNegative(boolean negative) {
            this.negative = negative;
        }

        /**
         *
         * @return
         */
        public Float getPower() {
            return power;
        }

        /**
         *
         * @param power
         */
        public void setPower(Float power) {
            this.power = power;
        }

        /**
         *
         * @return
         */
        @Override
        public String toString() {
            return (this.negative ? "-" : "") + this.value + (this.power == 0f ? "" : "|" + Float.toString(this.power));
        }

        /**
         *
         * @param t
         * @return
         */
        @Override
        public boolean equals(Object t) {
            if (t instanceof Tag) {
                return this.value.equals(((Tag) t).value);
            } else {
                throw new IllegalArgumentException("Compared object should be a Tag");
            }
        }

        /**
         *
         * @return
         */
        @Override
        public int hashCode() {
            return this.value.hashCode();
        }
    }
}
