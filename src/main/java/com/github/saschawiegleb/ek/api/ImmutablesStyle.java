package com.github.saschawiegleb.ek.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Target({ ElementType.PACKAGE, ElementType.TYPE })
@Retention(RetentionPolicy.CLASS)
@Value.Style(

    /** Detect 'get', 'has' and 'is' prefixes in accessor methods */
    get = { "get*", "has*", "is*" },

    /**
     * We use the alternative 'singleton' to be free to use 'of' in our abstract
     * types, because the generated type cannot reduce the visibility of the
     * inherited method from the abstract type.
     */
    instance = "singleton",

    /**
     * Forces extension of abstract value type class in generated signatures instead
     * of the immutable implementation class.
     */
    overshadowImplementation = true,

    /** 'Abstract' prefix will be detected and trimmed */
    typeAbstract = { "Abstract*" },

    /** Generated class will be always package-private */
    visibility = ImplementationVisibility.PACKAGE,

    /** Setup defaults for Datameer's immutable types */
    defaults = @Value.Immutable(builder = true, copy = true, intern = true, prehash = true, singleton = false)

)
public @interface ImmutablesStyle {
}
