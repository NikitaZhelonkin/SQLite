package ru.nikitazhelonkin.sqlite.compiler;


import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;

import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementScanner7;

/**
 * Created by nikita on 03.02.17.
 */

class  Visitor extends ElementScanner7<Void, Map<TypeElement, TableSpec>> {

    private Trees mTrees;

    public Visitor(ProcessingEnvironment env) {
        mTrees = Trees.instance(env);
    }


    void fixFieldAccess(Element element) {
        ((JCTree) mTrees.getTree(element)).accept(new TreeScanner() {
            @Override
            public void visitVarDef(JCTree.JCVariableDecl jcVariableDecl) {
                jcVariableDecl.mods.flags &= ~Flags.PRIVATE;
                super.visitVarDef(jcVariableDecl);
            }
        });
    }
}
