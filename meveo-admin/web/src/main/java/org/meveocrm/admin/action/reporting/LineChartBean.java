package org.meveocrm.admin.action.reporting;

import javax.inject.Named;

import org.meveocrm.model.dwh.LineChart;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.chart.LineChartModel;


@Named
@ViewScoped
public class LineChartBean extends ChartEntityBean<LineChart,LineChartModel,LineChartEntityModel> {

	private static final long serialVersionUID = -5171375359771241684L;
	
	public LineChartBean() {
		super(LineChart.class);
	}

	public LineChartBean(Class<LineChart> clazz) {
		super(clazz);
	}

}
