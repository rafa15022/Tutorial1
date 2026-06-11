package processor

import annotations.Greeting
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_23)
@SupportedAnnotationTypes("annotations.Greeting")
class GreetingProcessor : AbstractProcessor() {

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {

        val classMethodMap =
            mutableMapOf<TypeElement, MutableList<ExecutableElement>>()

        for (element in roundEnv.getElementsAnnotatedWith(Greeting::class.java)) {
            if (element is ExecutableElement) {
                val enclosingClass = element.enclosingElement as TypeElement

                classMethodMap
                    .computeIfAbsent(enclosingClass) { mutableListOf() }
                    .add(element)
            }
        }

        for ((classElement, methods) in classMethodMap) {
            generateKotlinWrapperClass(classElement, methods)
        }

        return true
    }

    private fun generateKotlinWrapperClass(
        classElement: TypeElement,
        methods: List<ExecutableElement>
    ) {
        val packageName =
            processingEnv.elementUtils.getPackageOf(classElement).toString()

        val originalClassName =
            classElement.simpleName.toString()

        val wrapperClassName =
            "${originalClassName}Wrapper"

        val originalClass =
            ClassName(packageName, originalClassName)

        val classBuilder =
            TypeSpec.classBuilder(wrapperClassName)
                .primaryConstructor(
                    FunSpec.constructorBuilder()
                        .addParameter("original", originalClass)
                        .build()
                )
                .addProperty(
                    PropertySpec.builder("original", originalClass)
                        .initializer("original")
                        .build()
                )
                .addModifiers(KModifier.PUBLIC)

        for (method in methods) {
            val methodName =
                method.simpleName.toString()

            val parameters =
                method.parameters.map { parameter ->
                    ParameterSpec.builder(
                        parameter.simpleName.toString(),
                        parameter.asType().asTypeName()
                    ).build()
                }

            val arguments =
                method.parameters.joinToString(", ") {
                    it.simpleName.toString()
                }

            val greetingMessage =
                method.getAnnotation(Greeting::class.java)?.message
                    ?: "Hello!"

            val methodBuilder =
                FunSpec.builder(methodName)
                    .addModifiers(KModifier.PUBLIC)
                    .addParameters(parameters)
                    .addStatement("println(%S)", greetingMessage)
                    .addStatement("original.%L(%L)", methodName, arguments)

            classBuilder.addFunction(methodBuilder.build())
        }

        val generatedFile =
            FileSpec.builder(packageName, wrapperClassName)
                .addType(classBuilder.build())
                .build()

        try {
            val generatedDirectory =
                processingEnv.options["kapt.kotlin.generated"]

            if (generatedDirectory != null) {
                generatedFile.writeTo(File(generatedDirectory))
            } else {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "kapt.kotlin.generated not found"
                )
            }
        } catch (exception: Exception) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Error generating Kotlin file: ${exception.message}"
            )
        }
    }
}