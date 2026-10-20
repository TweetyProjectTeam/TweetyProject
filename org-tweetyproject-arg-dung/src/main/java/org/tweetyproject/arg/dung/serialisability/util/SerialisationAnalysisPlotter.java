/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.serialisability.util;

import java.util.List;
import java.util.Map;

import org.tweetyproject.arg.dung.reasoner.SerialisedExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DungTheoryPlotter;
import org.tweetyproject.commons.Plotter;
import org.tweetyproject.commons.PlotterMultiFrame;
import org.tweetyproject.serialisability.SerialisationGraph;
import org.tweetyproject.serialisability.util.SerialisationGraphPlotter;

/**
 * This class summarizes static methods used to plot serialisability analyses,
 * consisting of plotting the underlying argumentation framework and its
 * serialisation graphs.
 *
 * @see DungTheoryPlotter
 * @see SerialisationGraphPlotter
 *
 * @author Julian Sander
 * @author Lars Bengel
 */
public class SerialisationAnalysisPlotter {

    /** Creates the utility class. */
    protected SerialisationAnalysisPlotter() {
        // default
    }

    /**
     * Plots the framework and its serialisation graphs in a new frame
     * @param framework an argumentation framework
     * @param graphs serialisation graphs of the framework
     * @param title title of the plots
     * @param width width of the frame
     * @param height height of the frame
     */
    public static void plotAnalyses(DungTheory framework, List<SerialisationGraph<DungTheory, Extension<DungTheory>>> graphs,
            String title, int width, int height) {
        plotAnalyses(Map.of(framework, graphs), title, width, height);
    }

    /**
     * Plots each framework with its serialisation graphs in a separate frame
     * @param graphs frameworks mapped to their serialisation graphs
     * @param title title of the plots, followed by the index of the framework
     * @param width width of the frames
     * @param height height of the frames
     */
    public static void plotAnalyses(Map<DungTheory, List<SerialisationGraph<DungTheory, Extension<DungTheory>>>> graphs,
            String title, int width, int height) {
        int index = 0;
        for (Map.Entry<DungTheory, List<SerialisationGraph<DungTheory, Extension<DungTheory>>>> entry : graphs.entrySet()) {
            Plotter groundPlotter = new Plotter();
            groundPlotter.createFrame(width, height);
            plotAnalysis(entry.getKey(), entry.getValue(), groundPlotter, title + " " + index++);
            groundPlotter.show();
        }
    }

    /**
     * Plots each framework with its serialisation graphs wrt. the given semantics in a separate frame
     * @param semantics serialisable semantics
     * @param frameworks argumentation frameworks
     * @param title title of the plots, followed by the index of the framework
     * @param width width of the frames
     * @param height height of the frames
     */
    public static void plotAnalyses(Semantics[] semantics, DungTheory[] frameworks, String title, int width, int height) {
        for (int i = 0; i < frameworks.length; i++) {
            Plotter groundPlotter = new Plotter();
            groundPlotter.createFrame(width, height);
            plotAnalysis(semantics, frameworks[i], groundPlotter, title + " " + i);
            groundPlotter.show();
        }
    }

    /**
     * Plots all frameworks with their serialisation graphs in a single frame
     * @param graphs frameworks mapped to their serialisation graphs
     * @param title title of the plots
     * @param width width of the frame
     * @param height height of the frame
     * @return the frame containing the plots
     */
    public static PlotterMultiFrame plotAnalysesOneFrame(
            Map<DungTheory, List<SerialisationGraph<DungTheory, Extension<DungTheory>>>> graphs, String title, int width, int height) {
        PlotterMultiFrame groundPlotter = new PlotterMultiFrame();
        groundPlotter.createFrame(width, height);
        for (Map.Entry<DungTheory, List<SerialisationGraph<DungTheory, Extension<DungTheory>>>> entry : graphs.entrySet())
            plotAnalysis(entry.getKey(), entry.getValue(), groundPlotter, title);
        groundPlotter.show();
        return groundPlotter;
    }

    /**
     * Plots all frameworks with their serialisation graphs wrt. the given semantics in a single frame
     * @param semantics serialisable semantics
     * @param frameworks argumentation frameworks
     * @param titles titles of the frameworks
     * @param width width of the frame
     * @param height height of the frame
     * @return the frame containing the plots
     */
    public static PlotterMultiFrame plotAnalysesOneFrame(Semantics[] semantics, DungTheory[] frameworks, String[] titles,
            int width, int height) {
        PlotterMultiFrame groundPlotter = new PlotterMultiFrame();
        groundPlotter.createFrame(width, height);
        for (int i = 0; i < frameworks.length; i++)
            plotAnalysis(semantics, frameworks[i], groundPlotter, titles[i]);
        groundPlotter.show();
        return groundPlotter;
    }

    /**
     * Plots the framework and the given serialisation graphs in the frame of the plotter
     * @param framework an argumentation framework
     * @param graphs serialisation graphs of the framework
     * @param groundPlotter the ground plotter
     * @param title title of the plots
     */
    private static void plotAnalysis(DungTheory framework, List<SerialisationGraph<DungTheory, Extension<DungTheory>>> graphs,
            Plotter groundPlotter, String title) {
        DungTheoryPlotter.plotFramework(framework, groundPlotter, title);
        for (SerialisationGraph<DungTheory, Extension<DungTheory>> graph : graphs)
            SerialisationGraphPlotter.plotGraph(graph, groundPlotter, title);
    }

    /**
     * Plots the framework and its serialisation graphs wrt. the given semantics in the frame of the plotter
     * @param semantics serialisable semantics
     * @param framework an argumentation framework
     * @param groundPlotter the ground plotter
     * @param title title of the plots
     */
    private static void plotAnalysis(Semantics[] semantics, DungTheory framework, Plotter groundPlotter, String title) {
        DungTheoryPlotter.plotFramework(framework, groundPlotter, title);
        for (Semantics s : semantics)
            SerialisationGraphPlotter.plotGraph(new SerialisedExtensionReasoner(s).getSerialisationGraph(framework),
                    groundPlotter, title + ": " + s.description());
    }
}
