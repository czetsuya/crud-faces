/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.manaty.view.composite;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.manaty.bean.BaseBean;
import org.manaty.model.IEntity;
import org.manaty.model.constraint.Email;
import org.manaty.model.constraint.Mask;
import org.manaty.service.PersistenceService;
import org.manaty.util.ParamBean;
import org.manaty.util.StringUtils;
import org.omnifaces.util.Faces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackingBeanBasedCompositeComponent extends UINamingContainer {

	private static final String BOOLEAN_TRUE_STRING = "true";

	private Logger log = LoggerFactory
			.getLogger(BackingBeanBasedCompositeComponent.class);

	@SuppressWarnings("rawtypes")
	private Class entityClass;

	ParamBean paramBean = ParamBean.getInstance();

	private Field field;
	private Field childField;
	private boolean embedded;
	private boolean message;
	private boolean manyToOne;
	private boolean manyToMany;
	private boolean email;
	private String regexPattern;
	private String maskValue;
	private String childMaskValue;

	private ResourceBundle resourceBundle = ResourceBundle.getBundle(
			"messages", Faces.getLocale());

	/**
	 * Get backing bean attribute either from parent component (search panel,
	 * thats where it usually should be defined) or from searchField component
	 * attributes (same with formPanel and formField).
	 */
	@SuppressWarnings("unchecked")
	public BaseBean<? extends IEntity, ? extends PersistenceService<?>> getBackingBeanFromParentOrCurrent() {

		BaseBean<? extends IEntity, ? extends PersistenceService<?>> backingBean = (BaseBean<? extends IEntity, ? extends PersistenceService<?>>) getStateHelper()
				.get("backingBean");
		if (backingBean == null) {
			backingBean = (BaseBean<? extends IEntity, ? extends PersistenceService<?>>) getAttributes()
					.get("backingBean");

			if (backingBean == null) {
				UIComponent parent = getCompositeComponentParent(this);
				if (parent != null
						&& parent instanceof BackingBeanBasedCompositeComponent) {
					backingBean = ((BackingBeanBasedCompositeComponent) parent)
							.getBackingBeanFromParentOrCurrent();
				}
			}
			if (backingBean == null) {
				throw new IllegalStateException(
						"No backing bean was set in parent or current composite component!");
			} else {
				getStateHelper().put("backingBean", backingBean);
			}
		}

		return backingBean;
	}

	/**
	 * Helper method to get entity from backing bean.
	 * 
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	public Object getEntityFromBackingBeanOrAttribute()
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {

		Object entity = getStateHelper().get("entity");
		if (entity == null) {
			entity = (Object) getAttributes().get("entity");

			if (entity == null) {
				UIComponent parent = getCompositeComponentParent(this);
				if (parent != null
						&& parent instanceof BackingBeanBasedCompositeComponent) {
					entity = ((BackingBeanBasedCompositeComponent) parent)
							.getEntityFromBackingBeanOrAttribute();
				}
			}

			if (entity == null && this instanceof FormPanelCompositeComponent) {
				try {
					entity = getBackingBeanFromParentOrCurrent().getEntity();
				} catch (Exception e) {
					log.error("Failed to instantiate a entity {}",
							e.getMessage());
				}
			}

			if (entity == null
					&& this instanceof SearchFormPanelCompositeComponent) {
				try {
					entity = getBackingBeanFromParentOrCurrent().getEntity();
				} catch (Exception e) {
					log.error("Failed to instantiate a entity {}",
							e.getMessage());
				}
			}

		}
		return entity;
	}

	/**
	 * Helper method to get entity instance to query field definitions.
	 */
	@SuppressWarnings("rawtypes")
	public Class getEntityClass() {
		if (entityClass == null) {
			entityClass = getBackingBeanFromParentOrCurrent().getEntityClass();
		}
		return entityClass;
	}

	/**
	 * Return date pattern to use for rendered date/calendar fields. If time
	 * attribute was set to true then this methods returns date/time pattern,
	 * otherwise only date without time pattern.
	 */
	public String getDatePattern() {
		if (getAttributes().get("time").equals(BOOLEAN_TRUE_STRING)) {
			return paramBean.getProperty("format.dateTime",
					"yyyy/MM/dd hh:mm:ss");
		} else {
			return paramBean.getProperty("format.date", "yyyy/MM/dd");
		}
	}

	public boolean setField(String fieldName, String childFieldName,
			boolean determineFromEntityClass) throws SecurityException,
			NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		return setField(fieldName, childFieldName, determineFromEntityClass,
				false);
	}

	public boolean setField(String fieldName, String childFieldName,
			boolean determineFromEntityClass, boolean message)
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {

		this.message = message;
		field = getBeanFieldThrowException(
				determineFromEntityClass ? getEntityClass()
						: getEntityFromBackingBeanOrAttribute().getClass(),
				fieldName);

		embedded = field.getAnnotation(Embedded.class) == null ? false : true;

		if (field.getAnnotation(Mask.class) != null) {
			maskValue = field.getAnnotation(Mask.class).value();
		}

		if (field.getAnnotation(Pattern.class) != null) {
			regexPattern = field.getAnnotation(Pattern.class).regexp();
		}
		if (field.getAnnotation(Email.class) != null) {
			email = true;
		}
		if (field.getAnnotation(ManyToOne.class) != null) {
			manyToOne = true;
		}
		if (field.getAnnotation(ManyToMany.class) != null) {
			manyToMany = true;
		}

		if (!StringUtils.isBlank(childFieldName)) {
			childField = getChildFieldType(field, childFieldName);
		}

		if (embedded) {
			if (childField.getAnnotation(Mask.class) != null) {
				maskValue = childField.getAnnotation(Mask.class).value();
			}
		}

		return false;
	}

	private Field getChildFieldType(Field parentField, String childFieldName)
			throws NoSuchFieldException, SecurityException {
		Field _childField = null;

		if (isManyToMany()) {
			ParameterizedType parameterType = (ParameterizedType) parentField
					.getGenericType();
			Class<?> parameterClass = (Class<?>) parameterType
					.getActualTypeArguments()[0];
			_childField = getBeanField(parameterClass, childFieldName);
		} else {
			_childField = parentField.getType()
					.getDeclaredField(childFieldName);
		}

		return _childField;
	}

	public boolean isMask() {
		return !StringUtils.isBlank(maskValue);
	}

	public boolean isMessage() {
		return message;
	}

	public boolean isManyToMany() {
		return manyToMany;
	}

	public boolean isManyToOne() {
		return manyToOne;
	}

	public boolean isText() throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		if (isEmbedded()) {
			return childField.getType() == String.class;
		} else {
			return field.getType() == String.class;
		}
	}

	public boolean isBoolean() throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		if (isEmbedded()) {
			Class<?> type = childField.getType();
			return type == Boolean.class
					|| (type.isPrimitive() && type.getName().equals("boolean"));
		} else {
			Class<?> type = field.getType();
			return type == Boolean.class
					|| (type.isPrimitive() && type.getName().equals("boolean"));
		}
	}

	public boolean isDate() throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		if (isEmbedded()) {
			return childField.getType() == Date.class;
		} else {
			return field.getType() == Date.class;
		}
	}

	public boolean isEnum() throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		if (isEmbedded()) {
			return childField.getType().isEnum();
		} else {
			return field.getType().isEnum();
		}
	}

	public boolean isInteger() throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {

		if (isEmbedded()) {
			Class<?> type = childField.getType();
			return type == Integer.class
					|| (type.isPrimitive() && type.getName().equals("int"));
		} else {
			Class<?> type = field.getType();
			return type == Integer.class
					|| (type.isPrimitive() && type.getName().equals("int"));
		}
	}

	public boolean isLong() throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		if (isEmbedded()) {
			Class<?> type = childField.getType();
			return type == Long.class
					|| (type.isPrimitive() && type.getName().equals("long"));
		} else {
			Class<?> type = field.getType();
			return type == Long.class
					|| (type.isPrimitive() && type.getName().equals("long"));
		}
	}

	public boolean isByte() throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		if (isEmbedded()) {
			Class<?> type = childField.getType();
			return type == Byte.class
					|| (type.isPrimitive() && type.getName().equals("byte"));
		} else {
			Class<?> type = field.getType();
			return type == Byte.class
					|| (type.isPrimitive() && type.getName().equals("byte"));
		}
	}

	public boolean isShort() throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		if (isEmbedded()) {
			Class<?> type = childField.getType();
			return type == Short.class
					|| (type.isPrimitive() && type.getName().equals("short"));
		} else {
			Class<?> type = field.getType();
			return type == Short.class
					|| (type.isPrimitive() && type.getName().equals("short"));
		}
	}

	public boolean isDouble() throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		if (isEmbedded()) {
			Class<?> type = childField.getType();
			return type == Double.class
					|| (type.isPrimitive() && type.getName().equals("double"));
		} else {
			Class<?> type = field.getType();
			return type == Double.class
					|| (type.isPrimitive() && type.getName().equals("double"));
		}
	}

	public boolean isFloat() throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		if (isEmbedded()) {
			Class<?> type = childField.getType();
			return type == Float.class
					|| (type.isPrimitive() && type.getName().equals("float"));
		} else {
			Class<?> type = field.getType();
			return type == Float.class
					|| (type.isPrimitive() && type.getName().equals("float"));
		}
	}

	public boolean isBigDecimal() throws SecurityException,
			NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		if (isEmbedded()) {
			return childField.getType() == BigDecimal.class;
		} else {
			return field.getType() == BigDecimal.class;
		}
	}

	public boolean isEntityField() throws SecurityException,
			NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		if (isEmbedded()) {
			return IEntity.class.isAssignableFrom(childField.getType());
		} else {
			return IEntity.class.isAssignableFrom(field.getType());
		}
	}

	public boolean isListField() throws SecurityException,
			NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		if (isEmbedded()) {
			Class<?> type = childField.getType();
			return type == List.class || type == Set.class;
		} else {
			Class<?> type = field.getType();
			return type == List.class || type == Set.class;
		}
	}

	public Object[] getEnumConstants(String fieldName)
			throws SecurityException, NoSuchFieldException {
		Object[] objArr = field.getType().getEnumConstants();
		Arrays.sort(objArr, new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});

		return objArr;
	}

	private Field getBeanFieldThrowException(Class<?> c, String fieldName)
			throws SecurityException, NoSuchFieldException {
		Field _field = getBeanField(c, fieldName);
		if (_field == null) {
			throw new IllegalStateException("No field with name '" + fieldName
					+ "' was found. EntityClass " + c);
		}
		return _field;
	}

	private Field getBeanField(Class<?> c, String fieldName)
			throws SecurityException, NoSuchFieldException {
		Field _field = null;
		try {
			_field = c.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			if (_field == null && c.getSuperclass() != null) {
				return getBeanField(c.getSuperclass(), fieldName);
			}
		}

		return _field;
	}

	public boolean isEmbedded() {
		return embedded;
	}

	/** Reflection Section **/
	public long getMaxLength() {
		try {
			if (isEmbedded()) {
				return childField.getAnnotation(Column.class).length();
			} else {
				return field.getAnnotation(Column.class).length();
			}
		} catch (NullPointerException e) {
			if (isEmbedded()) {
				return childField.getAnnotation(Size.class).max();
			} else {
				return field.getAnnotation(Size.class).max();
			}
		}
	}

	public boolean isRequired() {
		try {
			if (isEmbedded()) {
				return childField.getAnnotation(NotNull.class) == null ? false
						: true;
			} else {
				return field.getAnnotation(NotNull.class) == null ? false
						: true;
			}
		} catch (NullPointerException e) {
			return false;
		}
	}

	public String getFieldLabel() {
		try {
			if (isEmbedded()) {
				return resourceBundle.getString(("entity."
						+ removeProxy(getEntityFromBackingBeanOrAttribute()
								.getClass().getSimpleName().toLowerCase())
						+ "." + childField.getName()));
			} else {
				return resourceBundle.getString(("entity."
						+ removeProxy(getEntityFromBackingBeanOrAttribute()
								.getClass().getSimpleName().toLowerCase())
						+ "." + field.getName()));
			}

		} catch (Exception e) {
			try {
				log.error("label not found {}", ("entity."
						+ removeProxy(getEntityFromBackingBeanOrAttribute()
								.getClass().getSimpleName().toLowerCase())
						+ "." + field.getName()));
			} catch (SecurityException | NoSuchFieldException
					| IllegalArgumentException | IllegalAccessException
					| InvocationTargetException | NoSuchMethodException e1) {
			}
			try {
				if (isEmbedded()) {
					return resourceBundle
							.getString(("entity." + "common." + childField
									.getName()));
				} else {
					return resourceBundle
							.getString(("entity." + "common." + field.getName()));
				}
			} catch (MissingResourceException e1) {
				log.error(e1.getMessage());
			}
		}
		return "";
	}

	private String removeProxy(String label) {
		return label.replaceAll("_\\$\\$_javassist_\\d+", "");
	}

	public String getSearchFieldValue() {
		if (isEmbedded()) {
			return field.getName() + "." + childField.getName();
		} else {
			return field.getName();
		}
	}

	private Object evalExpressionGet(String p_expression) {
		FacesContext context = FacesContext.getCurrentInstance();
		ExpressionFactory expressionFactory = context.getApplication()
				.getExpressionFactory();
		ELContext elContext = context.getELContext();
		ValueExpression vex = expressionFactory.createValueExpression(
				elContext, p_expression, Object.class);

		Object result = (Object) vex.getValue(elContext);
		return result;
	}

	private void evalExpressionSet(String p_expression, Object value) {
		FacesContext context = FacesContext.getCurrentInstance();
		ExpressionFactory expressionFactory = context.getApplication()
				.getExpressionFactory();
		ELContext elContext = context.getELContext();
		ValueExpression vex = expressionFactory.createValueExpression(
				elContext, p_expression, Object.class);

		vex.setValue(elContext, value);
	}

	public String getFormFieldKey() {
		return field.getName();
	}

	public String getFormChildFieldKey() {
		return childField.getName();
	}

	public Object getFormFieldValue() {
		try {
			if (isEmbedded()) {
				return evalExpressionGet("#{cc.entityFromBackingBeanOrAttribute[cc.formFieldKey][cc.formChildFieldKey]}");
			} else if (isManyToOne()) {
				return evalExpressionGet("#{cc.entityFromBackingBeanOrAttribute[cc.formFieldKey]}");
			} else {
				return evalExpressionGet("#{cc.entityFromBackingBeanOrAttribute[cc.formFieldKey]}");
			}
		} catch (NullPointerException e) {
			return null;
		}
	}

	public Object getFormFieldValueForDisplay() {
		try {
			if (isEmbedded()) {
				Object obj = evalExpressionGet("#{cc.entityFromBackingBeanOrAttribute[cc.formFieldKey][cc.formChildFieldKey]}");
				try {
					return resourceBundle.getString(obj.toString());
				} catch (MissingResourceException e) {
					return obj;
				}
			} else if (isManyToOne()) {
				return evalExpressionGet("#{cc.entityFromBackingBeanOrAttribute[cc.formFieldKey][cc.formChildFieldKey]}");
			} else {
				return evalExpressionGet("#{cc.entityFromBackingBeanOrAttribute[cc.formFieldKey]}");
			}
		} catch (NullPointerException e) {
			return null;
		}
	}

	public void setFormFieldValue(Object value) {
		try {
			if (isEmbedded()) {
				evalExpressionSet(
						"#{cc.entityFromBackingBeanOrAttribute[cc.formFieldKey][cc.formChildFieldKey]}",
						value);
			} else if (isManyToOne()) {
				evalExpressionSet(
						"#{cc.entityFromBackingBeanOrAttribute[cc.formFieldKey]}",
						value);
			} else {
				evalExpressionSet(
						"#{cc.entityFromBackingBeanOrAttribute[cc.formFieldKey]}",
						value);
			}
		} catch (NullPointerException e) {

		}
	}

	public String getFormTitle() {
		try {
			return resourceBundle.getString("entity."
					+ removeProxy(getEntityFromBackingBeanOrAttribute()
							.getClass().getSimpleName().toLowerCase()));
		} catch (Exception e) {
			return "";
		}
	}

	public String getSearchTitle() {
		try {
			return resourceBundle.getString("commons.search")
					+ " "
					+ removeProxy(resourceBundle.getString("entity."
							+ getEntityFromBackingBeanOrAttribute().getClass()
									.getSimpleName().toLowerCase()));
		} catch (Exception e) {
			return "";
		}
	}

	public long getMaxValue() {
		try {
			return field.getAnnotation(Max.class).value();
		} catch (NullPointerException e) {
			return 0;
		}
	}

	public long getMinValue() {
		try {
			return field.getAnnotation(Min.class).value();
		} catch (NullPointerException e) {
			return 0;
		}
	}

	public Object getItemLabel(Object label) throws NoSuchFieldException,
			SecurityException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		if (isMessage()) {
			try {
				return resourceBundle.getString(label.toString());
			} catch (NullPointerException e) {
				return null;
			}
		} else {
			Field _field = label.getClass().getDeclaredField(
					childField.getName());
			_field.setAccessible(true);
			return _field.get(label);
		}
	}

	public String getRegexPattern() {
		return regexPattern;
	}

	public String getMaskValue() {
		if (maskValue.startsWith("$$_")) {
			return resourceBundle.getString(maskValue);
		} else {
			return maskValue;
		}
	}

	/** End Reflection Section **/

	public String getChildMaskValue() {
		return childMaskValue;
	}

	public void setChildMaskValue(String childMaskValue) {
		this.childMaskValue = childMaskValue;
	}

	public boolean isEmail() {
		return email;
	}

	public void setEmail(boolean email) {
		this.email = email;
	}
}
