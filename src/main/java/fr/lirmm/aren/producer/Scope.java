package fr.lirmm.aren.producer;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

/**
 *
 * @author florent
 */
@Qualifier
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface Scope {

    Type value();

    enum Type {
        REQUEST, SESSION, APPLICATION
    };
}
