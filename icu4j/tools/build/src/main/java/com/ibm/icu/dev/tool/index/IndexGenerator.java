// © 2016 and later: Unicode, Inc. and others.
// License & terms of use: http://www.unicode.org/copyright.html
/**
*******************************************************************************
* Copyright (C) 2005-2010, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*/
package com.ibm.icu.dev.tool.index;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class IndexGenerator {
    
    private final static String stoplist = ",char.res,CurrencyData.res,invuca.res,line.res,line_th.res,pnames.res,res_index.res,sent.res,title.res,ucadata.res,ucase.res,uidna.res,unames.res,unorm.res,uprops.res,word.res,word_ja.res,word_POSIX.res,word_th.res";

    public static void main(String[] args) {
        if (args.length < 1) {
            usage("too few arguments");
        }

        File inDir = new File(args[0]);
        if (!inDir.exists()) {
            System.out.println("skipping nonexistent directory " + inDir);
            return;
        }

        if (!inDir.isDirectory()) {
            usage("first argument '" + inDir + "' must be a directory");
        }

        File outDir = inDir;
        if (args.length > 1) {
            outDir = new File(args[1]);
            if (!outDir.isDirectory() || !outDir.exists()) {
                usage("second argument must be existing directory");
            }
        }

        Set names = new TreeSet();
        File[] files = inDir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++){
                if (!files[i].isDirectory()) {
                    String name = "," + files[i].getName(); // add ',' to get exact match
                    if (name.endsWith(".res") && stoplist.indexOf(name) == -1) {
                        names.add(name.substring(1, name.lastIndexOf('.'))); // 1 to trim off ','
                    }
                }
            }
        }

        DateFormat fmt = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.US);
        DateFormat copyfmt = new SimpleDateFormat("'# Copyright (C) 'yyyy' IBM Inc.  All Rights Reserved.'");

        try {
            File outFile = new File(outDir, "res_index.txt");
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
            Date now = new Date();
            pw.println("# Generated by " + IndexGenerator.class.getName() + " on " + fmt.format(now));
            pw.println("# from contents of " + inDir.getCanonicalPath());
            pw.println(copyfmt.format(now));
            Iterator i = names.iterator();
            while (i.hasNext()) {
                pw.println(i.next());
            }
            int count = names.size();
            pw.println("# Found " + count + " files");
            pw.println("# End of file");
            if (count == 0) {
                System.err.println("Warning: matched no files");
            } 
            pw.close();
        }
        catch (IOException e) {
            usage(e.getMessage());
        }
    }

    private static void usage(String msg) {
        if (msg != null) {
            System.err.println("Error: " + msg);
        }
        System.out.println("Usage: IndexGenerator inDir outDir");
        System.out.println("  inDir is an existing directory whose locale-based resources are to be enumerated");
        System.out.println("  outDir is an existing directory in which the res_index.txt file will be placed");
        throw new IllegalStateException("Usage");
    }
}

