/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.rest.spring.handler;

import static org.androidannotations.helper.ModelConstants.classSuffix;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.handler.MethodInjectionHandler;
import org.androidannotations.helper.InjectHelper;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.annotations.RestService;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JExpr;

public class RestServiceHandler extends BaseAnnotationHandler<EComponentHolder>implements MethodInjectionHandler<EComponentHolder> {

	private final InjectHelper<EComponentHolder> injectHelper;

	public RestServiceHandler(AndroidAnnotationsEnvironment environment) {
		super(RestService.class, environment);
		injectHelper = new InjectHelper<>(validatorHelper, this);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		injectHelper.validate(RestService.class, element, validation);

		validatorHelper.isNotPrivate(element, validation);

		Element param = injectHelper.getParam(element);
		validatorHelper.typeHasAnnotation(Rest.class, param, validation);
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		injectHelper.process(element, holder);
	}

	@Override
	public JBlock getInvocationBlock(EComponentHolder holder) {
		return holder.getInitBodyInjectionBlock();
	}

	@Override
	public void assignValue(JBlock targetBlock, IJAssignmentTarget fieldRef, EComponentHolder holder, Element element, Element param) {
		TypeMirror fieldTypeMirror = param.asType();
		TypeMirror erasedFieldTypeMirror = getProcessingEnvironment().getTypeUtils().erasure(fieldTypeMirror);
		String interfaceName = erasedFieldTypeMirror.toString();

		String generatedClassName = interfaceName + classSuffix();

		AbstractJClass clazz = codeModelHelper.narrowGeneratedClass(getJClass(generatedClassName), fieldTypeMirror);

		targetBlock.add(fieldRef.assign(JExpr._new(clazz).arg(holder.getContextRef())));
	}

	@Override
	public void validateEnclosingElement(Element element, ElementValidation valid) {
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, valid);
	}
}
