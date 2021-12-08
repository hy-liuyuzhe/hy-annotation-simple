package com.yuwq.apt_processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.yuwq.apt_annotation.BindView;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;


/**
 * @author liuyuzhe
 */
@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {

    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> hashSet = new HashSet<>();
        hashSet.add(BindView.class.getCanonicalName());
        return hashSet;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("liuyuzhe process");
        Set<? extends Element> elementSet = roundEnv.getElementsAnnotatedWith(BindView.class);
        Map<TypeElement, Map<Integer, VariableElement>> typeElementMap = new HashMap<>();

        for (Element element : elementSet) {
            VariableElement variableElement = (VariableElement) element;
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
            System.out.println("liuyuzhe: " + typeElement);
            Map<Integer, VariableElement> variableElementMap = typeElementMap.get(typeElement);
            if (variableElementMap == null) {
                variableElementMap = new HashMap<>();
                typeElementMap.put(typeElement, variableElementMap);
            }
            BindView bindView = variableElement.getAnnotation(BindView.class);
            int viewId = bindView.value();
            variableElementMap.put(viewId, variableElement);
        }
        System.out.println("liuyuzhe.typeElementMap: "+typeElementMap);
        for (TypeElement key : typeElementMap.keySet()) {
            Map<Integer, VariableElement> variableElementMap = typeElementMap.get(key);
            String packageName = ElementUtils.getPackageName(elementUtils, key);
            JavaFile javaFile = JavaFile.builder(packageName, generateCodeByPoet(key, variableElementMap)).build();

            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("liuyuzhe.BindViewProcessor "+e.getMessage());
            }
        }
        System.out.println("liuyuzhe.return ");
        return true;
    }

    private TypeSpec generateCodeByPoet(TypeElement typeElement, Map<Integer, VariableElement> variableElementMap) {
        return TypeSpec.classBuilder(ElementUtils.getEnclosingClassName(typeElement) + "ViewBinding")
                .addModifiers(Modifier.PUBLIC).addMethod(generateMethodByPoet(typeElement, variableElementMap)).build();


    }

    private MethodSpec generateMethodByPoet(TypeElement typeElement, Map<Integer, VariableElement> variableElementMap) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind");
        ClassName className = ClassName.bestGuess(typeElement.getQualifiedName().toString());
        String parameter = "_"+toLowerCaseFirstChar(className.simpleName());
        methodBuilder.addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .returns(void.class).addParameter(className,parameter);
        for(int  viewId: variableElementMap.keySet()){
            VariableElement element = variableElementMap.get(viewId);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            String pattern = "{0}.{1}=({2})({3}.findViewById({4}));";
            methodBuilder.addCode(MessageFormat.format(pattern, parameter, name, type, parameter, String.valueOf(viewId)));
        }
        return methodBuilder.build();
    }


    public String toLowerCaseFirstChar(String name){
        if (name == null || name.length() == 0) return name;

        if (Character.isLowerCase(name.charAt(0))){
            return name;
        }
        return Character.toLowerCase(name.charAt(0))+name.substring(1);
    }
}
