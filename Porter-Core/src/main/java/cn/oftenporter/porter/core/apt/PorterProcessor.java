package cn.oftenporter.porter.core.apt;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by https://github.com/CLovinr on 2016/9/8.
 */
@SupportedAnnotationTypes({"cn.oftenporter.porter.core.apt.AutoParser"})
public class PorterProcessor extends AbstractProcessor
{
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        final String suffix = "Impl";
        outer:
        for (TypeElement element : annotations)
        {
            if (element.getKind() != ElementKind.INTERFACE)
            {
                System.err.println("just for interface!(current:" + element.getQualifiedName());
                continue;
            }
            try
            {
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
                        element.getQualifiedName() + suffix);
                BufferedWriter bw = new BufferedWriter(jfo.openWriter());
                PackageElement packageElement =
                        (PackageElement) element.getEnclosingElement();
                bw.append("package ").append(packageElement.getQualifiedName()).append(';');
                bw.newLine();
                bw.append("public class ").append(element.getSimpleName()).append(suffix).append(" implements ")
                        .append(element.getSimpleName());
                bw.append('{');
                bw.newLine();

                List<? extends Element> list = element.getEnclosedElements();
                for (int k = 0; k < list.size(); k++)
                {
                    Element el = list.get(k);
                    if (el.getKind() == ElementKind.METHOD)
                    {
                        addMethodCode(bw,el);
                    }else if(el.getKind()==ElementKind.FIELD){
                        System.err.println("not support interface field("+el.getSimpleName()+")");
                        continue outer;
                    }
                }

                bw.newLine();
                bw.append('}');
            } catch (IOException e)
            {
                e.printStackTrace();
            }


        }
        System.out.println(new Date());
        return true;
    }

    private void addMethodCode(BufferedWriter bw, Element el)
    {
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }
}
