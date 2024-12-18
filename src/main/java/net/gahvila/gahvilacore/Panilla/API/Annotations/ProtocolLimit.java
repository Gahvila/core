package net.gahvila.gahvilacore.Panilla.API.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for marking methods that define protocol-related limits,
 * but are not directly part of the protocol logic.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProtocolLimit {
    String description() default "Protocol-related limits, not part of the protocol itself.";
}
