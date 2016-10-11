package org.meveo.api.security.Interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.meveo.api.security.filter.NullFilter;
import org.meveo.api.security.filter.SecureMethodResultFilter;
import org.meveo.api.security.parameter.SecureMethodParameter;
import org.meveo.model.admin.User;

/**
 * Identifies API methods that require proper user permissions to access.
 *
 * @author Tony Alejandro
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface SecuredBusinessEntityMethod {

	/**
	 * Contains an array of {@link SecureMethodParameter} annotations that
	 * describe how the method parameters are going to be validated.
	 * 
	 * @return
	 */
	SecureMethodParameter[] validate();

	/**
	 * This is a {@link SecureMethodParameter} instance that describes where to
	 * extract the {@link User} instance from the method parameters.
	 * 
	 * @return
	 */
	SecureMethodParameter user();

	/**
	 * The result filter class that will be used to filter the results for
	 * entities that should be accessible to the user.
	 * 
	 * @return
	 */
	Class<? extends SecureMethodResultFilter> resultFilter() default NullFilter.class;
}