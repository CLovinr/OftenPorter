package cn.oftenporter.porter.core.apt;

import cn.oftenporter.porter.core.annotation.PortInObj;
import cn.oftenporter.porter.core.util.WPTool;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.Closeable;
import java.util.*;

/**
 * Created by https://github.com/CLovinr on 2016/9/8.
 */
@SupportedAnnotationTypes({"cn.oftenporter.porter.core.apt.AutoGen"})
public class PorterProcessor extends AbstractProcessor
{
    public static final String SUFFIX = "AP";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        final String suffix = SUFFIX;
        System.out.println("AutoGen process...");
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "AutoGen process...");

        outer:
        for (TypeElement element : annotatedElementsIn(roundEnv))
        {
            System.out.println("deal:" + element);
            Closeable closeable = null;
            try
            {

                PackageElement packageElement =
                        (PackageElement) element.getEnclosingElement();
                SourceGenerator sourceGenerator = new SourceGenerator();
                sourceGenerator.init(packageElement.getQualifiedName().toString(),
                        element.getSimpleName().toString());

                List<? extends Element> list = element.getEnclosedElements();
                for (int k = 0; k < list.size(); k++)
                {
                    Element el = list.get(k);
                    if (el.getKind() == ElementKind.METHOD)
                    {
                        ExecutableElement executableElement = (ExecutableElement) el;
                        if (!executableElement.getModifiers().contains(Modifier.ABSTRACT))
                        {
                            continue;
                        }
                        if (isNece(executableElement))
                        {
                            sourceGenerator.addNeceMethod(executableElement);
                        } else
                        {
                            sourceGenerator.addUnNeceMethod(executableElement);
                        }
                    } else if (el.getKind() == ElementKind.FIELD)
                    {
                        System.err.println("not support interface field(" + el + ")");
                        continue outer;
                    }
                }

                //创建java源文件
                Filer filer = processingEnv.getFiler();

                JavaFileObject jfo = filer.createSourceFile(
                        element.getQualifiedName() + suffix, element);
                sourceGenerator.setWriter(jfo.openWriter());
                closeable = sourceGenerator;

                sourceGenerator.append("//" + new Date() + "\n");
                sourceGenerator.write();
                sourceGenerator.flush();
            } catch (GenException e)
            {
                processingEnv.getMessager()
                        .printMessage(Diagnostic.Kind.ERROR, "ex:" + e.getMessage(), element);
            } catch (Exception e)
            {
                processingEnv.getMessager()
                        .printMessage(Diagnostic.Kind.ERROR, "ex:create source file failed!\n" + e.toString(), element);
                e.printStackTrace();
            } finally
            {
                WPTool.close(closeable);
                System.out.println("AutoGen process end!");
            }


        }
        return true;
    }

    private boolean isNece(ExecutableElement element)
    {
        boolean isNece = true;

        if (element.getAnnotation(PortInObj.UnNece.class) != null)
        {
            isNece = false;
        }

        return isNece;
    }

    private Set<? extends TypeElement> annotatedElementsIn(
            RoundEnvironment roundEnv)
    {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(AutoGen.class);
        Set<TypeElement> set = new HashSet<>();
        Iterator<? extends Element> it = elements.iterator();
        while (it.hasNext())
        {
            Element element = it.next();
            if (!needParse(element))
            {
                processingEnv.getMessager()
                        .printMessage(Diagnostic.Kind.WARNING, "will not generator source.", element);
                continue;
            }

            if (element.getKind() == ElementKind.INTERFACE)
            {
                set.add(TypeElement.class.cast(element));
            } else
            {
                processingEnv.getMessager()
                        .printMessage(Diagnostic.Kind.WARNING, "just for interface!(current:" + element + ")", element);
            }
        }
        return set;
    }

    private boolean needParse(Element element)
    {
        List<? extends AnnotationMirror> list = element.getAnnotationMirrors();
        AnnotationMirror anno = null;
        for (int i = 0; i < list.size(); i++)
        {
            AnnotationMirror am = list.get(i);
            TypeElement typeElement = (TypeElement) am.getAnnotationType().asElement();
            if (typeElement.getQualifiedName().contentEquals(AutoGen.class.getName()))
            {
                anno = am;
                break;
            }
        }
        boolean needParse = true;
        if (anno != null)
        {

            Map<? extends ExecutableElement, ? extends AnnotationValue> values = anno.getElementValues();
            Iterator<? extends Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> it = values.entrySet()
                    .iterator();
            while (it.hasNext())
            {
                Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry = it.next();
                TypeElement type = (TypeElement) ((DeclaredType) (entry.getKey().getReturnType())).asElement();
                if (type.getQualifiedName().contentEquals(String.class.getName()))
                {
                    needParse = "".equals(entry.getValue());
                } else if (type.getQualifiedName().contentEquals(Class.class.getName()))
                {
                    needParse = entry.getValue().equals(AutoGen.class);
                }

                if (!needParse)
                {
                    break;
                }

            }
        }
        return needParse;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        Set<String> set = new HashSet<>(1);
        set.add(AutoGen.class.getName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }
}
