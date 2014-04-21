package com.blackducksoftware.soleng.bdsplugin.model;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.Rotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.charts.AbstractChart;
import org.sonar.api.charts.ChartParameters;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;

public class CCPieChart3D extends AbstractChart {

  private static final Logger log = LoggerFactory.getLogger(CCPieChart3D.class);	
	
  public static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 12);

  public static final String DEFAULT_MESSAGE_NODATA = "No data available";
  public static final String PARAM_VALUES = "v";
  public static final String PARAM_COLORS = "c";

  public String getKey() {
    return "pieChart3D";
  }

  @Override
  protected Plot getPlot(ChartParameters params) 
  {

	 DefaultPieDataset set = new DefaultPieDataset();

	 // Not really hex, but should work
    //String[] colorsHex = new String[] {"red", "orange", "green", "gray"};
	 
	String[] colorsHex = params.getValues(PARAM_COLORS, ",", true);
    String[] serie = params.getValues(PARAM_VALUES, ";", true);
    

    Color[] colors = COLORS;
    if (colorsHex != null && colorsHex.length > 0) 
    {
	      colors = new Color[colorsHex.length];
	      
	      for (int i = 0; i < colorsHex.length; i++) 
	      {
	          colors[i] = Color.decode("#" + colorsHex[i]);
	          log.info("Created color: " + colors[i].toString());
	      }
    }

    
    
	  
    JFreeChart chart3 = ChartFactory.createPieChart3D("License Chart", null, false, false, false);
    PiePlot3D plot = (PiePlot3D) chart3.getPlot();
    plot.setForegroundAlpha(0.6f);


    /**
     * This will parse through the passed in values and create a key/value pair
     * The assumption is that the original passed in value is of format: "key=value;key2=value2;key3=value3"
     * The 'serie' array is already split along the ';" token, all is left is to create the pair
     */
    String[] keyValue = null;
    for (int i = 0; i < serie.length; i++) 
    {
	      if (!StringUtils.isEmpty(serie[i])) 
	      {
		        keyValue = StringUtils.split(serie[i], "=");
		        log.info("Found keyValue pair: " + keyValue);
		        
		        set.setValue(keyValue[0], Double.parseDouble(keyValue[1]));
		        plot.setSectionPaint(keyValue[0], colors[i]);
	      }
    }
    
    
    
    plot.setDataset(set);

    PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator("{0}: {1}");
	plot.setLabelGenerator(generator); 
    
    plot.setStartAngle(360);
    plot.setCircular(true);
    plot.setDirection(Rotation.CLOCKWISE);
    plot.setNoDataMessage(DEFAULT_MESSAGE_NODATA);
    plot.setInsets(RectangleInsets.ZERO_INSETS);
    plot.setBackgroundAlpha(0.0f);
    plot.setIgnoreNullValues(true);
    plot.setIgnoreZeroValues(true);
    plot.setOutlinePaint(Color.WHITE);
    plot.setShadowPaint(Color.WHITE);
    plot.setDarkerSides(false);
    plot.setLabelFont(DEFAULT_FONT);
    plot.setLabelPaint(Color.BLACK);
    plot.setLabelBackgroundPaint(Color.WHITE);
    plot.setLabelOutlinePaint(Color.WHITE);
    plot.setLabelShadowPaint(Color.WHITE);
    plot.setLabelPadding(new RectangleInsets(1, 1, 1, 1));
    plot.setInteriorGap(0.02);
    plot.setMaximumLabelWidth(0.15);

    return plot;
  }

}
