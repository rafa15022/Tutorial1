package processor

import annotations.Extract
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
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
@SupportedAnnotationTypes("annotations.Extract")
class RegexProcessor : AbstractProcessor() {

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {

        val classMethodMap =
            mutableMapOf<TypeElement, MutableList<ExecutableElement>>()

        for (element in roundEnv.getElementsAnnotatedWith(Extract::class.java)) {
            if (element is ExecutableElement) {
                val enclosingClass = element.enclosingElement as TypeElement

                classMethodMap
                    .computeIfAbsent(enclosingClass) { mutableListOf() }
                    .add(element)
            }
        }

        for ((classElement, methods) in classMethodMap) {
            generateExtractorClass(classElement, methods)
        }

        return true
    }

    private fun generateExtractorClass(
        classElement: TypeElement,
        methods: List<ExecutableElement>
    ) {
        val packageName =
            processingEnv.elementUtils.getPackageOf(classElement).toString()

        val originalClassName =
            classElement.simpleName.toString()

        val extractorClassName =
            "${originalClassName}Extractor"

        val originalClass =
            ClassName(packageName, originalClassName)

        val classBuilder =
            TypeSpec.classBuilder(extractorClassName)
                .primaryConstructor(
                    FunSpec.constructorBuilder()
                        .addParameter("input", STRING)
                        .build()
                )
                .superclass(originalClass)
                .addSuperclassConstructorParameter("input")
                .addModifiers(KModifier.PUBLIC)

        for (method in methods) {
            val methodName =
                method.simpleName.toString()

            val regex =
                method.getAnnotation(Extract::class.java)?.regex
                    ?: continue

            val methodBuilder =
                FunSpec.builder(methodName)
                    .addModifiers(
                        KModifier.PUBLIC,
                        KModifier.OVERRIDE
                    )
                    .returns(STRING.copy(nullable = true))
                    .addStatement(
                        "val match = Regex(%S).find(input)",
                        regex
                    )
                    .addStatement(
                        "return match?.groupValues?.get(1)"
                    )

            classBuilder.addFunction(methodBuilder.build())
        }

        val generatedFile =
            FileSpec.builder(packageName, extractorClassName)
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
                "Error generating Regex class: ${exception.message}"
            )
        }
    }
}