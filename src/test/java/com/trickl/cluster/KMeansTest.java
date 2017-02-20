/*
 * This file is part of the Trickl Open Source Libraries.
 *
 * Trickl Open Source Libraries - http://open.trickl.com/
 *
 * Copyright (C) 2011 Tim Gee.
 *
 * Trickl Open Source Libraries are free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Trickl Open Source Libraries are distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.trickl.cluster;

import com.trickl.cluster.KMeans;
import cern.colt.matrix.DoubleMatrix2D;
import com.trickl.dataset.GaussianCircles2D;
import com.trickl.cluster.stats.Partition;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.junit.Test;
import org.apache.commons.math3.random.MersenneTwister;

public class KMeansTest {

   public KMeansTest() {
   }

   @Test
   public void ClusterGaussianCircles() throws IOException
   {
      GaussianCircles2D gaussianCircles = new GaussianCircles2D();
      // Set seed for repeatable results
      gaussianCircles.setRandomGenerator(new MersenneTwister(123456789));
      gaussianCircles.setRadiusStd(0.20);
      DoubleMatrix2D data = gaussianCircles.generate(100);

      KMeans km = new KMeans();      
      km.setRandomGenerator(new MersenneTwister(123456789));
      km.cluster(data, 3);

      DoubleMatrix2D partition = km.getPartition();
      System.out.println("Partition index: " + Partition.partitionCoefficient(partition));
      System.out.println("Partition entropy: " + Partition.partitionEntropy(partition));
      System.out.println("Xie-Beni index: " + Partition.xieBeniIndex(partition, km.getMeans().viewDice(), data, 1));

      // Output the membership data to separate files
      for (int k = 0; k < partition.columns(); ++k)
      {
         String fileName = "kmeans-cluster-" + k + ".dat";
         String packagePath = this.getClass().getPackage().getName().replaceAll("\\.", "/");
         File outputFile = new File("src/test/resources/"
              + packagePath
              + "/" + fileName);
         PrintWriter writer = new PrintWriter(outputFile);

         for (int i = 0; i < data.rows(); ++i)
         {
            StringBuilder dataLine = new StringBuilder();
            dataLine.append(data.getQuick(i, 0));

            for (int j = 1; j < data.columns(); ++j)
            {
               dataLine.append(' ');
               dataLine.append(data.getQuick(i, j));
            }

            dataLine.append(' ');
            dataLine.append(partition.getQuick(i, k));
            writer.println(dataLine.toString());
         }

         writer.close();
      }
   }
}
