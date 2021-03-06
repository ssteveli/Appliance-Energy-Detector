/*******************************************************************************
 * This file is part of the Appliance Energy Detector, a free household appliance energy disaggregation intelligence engine and webapp.
 * 
 * Copyright (C) 2011,2012 Taylor Raack <traack@raack.info>
 * 
 * The Appliance Energy Detector is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * The Appliance Energy Detector is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with the Appliance Energy Detector.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * According to sec. 7 of the GNU Affero General Public License, version 3, the terms of the AGPL are supplemented with the following terms:
 * 
 * If you modify this Program, or any covered work, by linking or combining it with any of the following programs (or modified versions of those libraries), containing parts covered by the terms of those libraries licenses, the licensors of this Program grant you additional permission to convey the resulting work:
 * 
 * Javabeans(TM) Activation Framework 1.1 (activation) - Common Development and Distribution License Version 1.0
 * AspectJ 1.6.9 (aspectjrt and aspectjweaver) - Eclipse Public License 1.0
 * EMMA 2.0.5312 (emma and emma_ant) - Common Public License Version 1.0
 * JAXB Project Libraries 2.2.2 (jaxb-api, jaxb-impl, jaxb-xjc) - Common Development and Distribution License Version 1.0
 * Java Standard Template Library 1.2 (jstl) - Common Development and Distribution License Version 1.0
 * Java Servlet Pages API 2.1 (jsp-api) - Common Development and Distribution License Version 1.0
 * Java Transaction API 1.1 (jta) - Common Development and Distribution License Version 1.0
 * JavaMail(TM) 1.4.1 (mail) - Common Development and Distribution License Version 1.0
 * XML Pull Parser 3 (xpp3) - Indiana University Extreme! Lab Software License Version 1.1.1
 * 
 * The interactive user interface of the software display an attribution notice containing the phrase "Appliance Energy Detector". Interactive user interfaces of unmodified and modified versions must display Appropriate Legal Notices according to sec. 5 of the GNU Affero General Public License, version 3, when you propagate an unmodified or modified version of the Program. In accordance with sec. 7 b) of the GNU Affero General Public License, version 3, these Appropriate Legal Notices must prominently display either a) "Initial Development by <a href='http://www.linkedin.com/in/taylorraack'>Taylor Raack</a>" if displayed in a web browser or b) "Initial Development by Taylor Raack (http://www.linkedin.com/in/taylorraack)" if displayed otherwise.
 ******************************************************************************/
package info.raack.appliancelabeler.machinelearning.appliancedetection.algorithms;

import info.raack.appliancedetection.common.util.DateUtils;
import info.raack.appliancelabeler.data.Database;
import info.raack.appliancelabeler.data.batch.ItemReader;
import info.raack.appliancelabeler.model.AlgorithmPredictions;
import info.raack.appliancelabeler.model.EnergyTimestep;
import info.raack.appliancelabeler.model.energymonitor.EnergyMonitor;
import info.raack.appliancelabeler.service.DataService.LabelResult;

import java.util.Calendar;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.cmu.hcii.stepgreen.data.ted.data.SecondData;

public abstract class ApplianceEnergyConsumptionDetectionAlgorithm {
	private Logger logger = LoggerFactory.getLogger(ApplianceEnergyConsumptionDetectionAlgorithm.class);
	
	@Autowired
	private Database database;
	
	@Autowired
	private DateUtils dateUtils;
	
	public static double missingValue = Double.NaN;

	public abstract int getId();
	protected abstract AlgorithmPredictions algorithmCalculateApplianceEnergyUsePredictions(EnergyMonitor monitor, Queue<EnergyTimestep> timesteps, ItemReader<SecondData> measurements);
	public abstract AlgorithmResult train(EnergyMonitor monitor, ItemReader<SecondData> dataReader);
	public abstract LabelResult detectAcceptableUserTraining(ItemReader<SecondData> dataReader);
	
	public AlgorithmPredictions calculateApplianceEnergyUsePredictions(EnergyMonitor monitor, Calendar firstMeasurementDate, Calendar lastMeasurementDate, ItemReader<SecondData> dataReader) {

		logger.debug("First measurement date: " + firstMeasurementDate.getTime() + "; last measurement date: " + lastMeasurementDate.getTime());
		// when is the last 5 minute increment before the first measurement
		Calendar startDate = dateUtils.getPreviousFiveMinuteIncrement(firstMeasurementDate);
		Calendar endDate = (Calendar)startDate.clone();
		endDate.add(Calendar.MINUTE, 5);
		
		// get all of the measurements since that point and add them into the measurements list
		dataReader.addBeginningItems(database.getEnergyMeasurementsForMonitor(monitor, startDate.getTime(), endDate.getTime(), 300));
		
		return algorithmCalculateApplianceEnergyUsePredictions(monitor, getEnergyTimesteps(firstMeasurementDate, lastMeasurementDate), dataReader);
	}
	
	public abstract String getAlgorithmName();
	
	public String toString() {
		return getAlgorithmName();
	}
	
	protected Queue<EnergyTimestep> getEnergyTimesteps(Calendar firstMeasurementDate, Calendar lastMeasurementDate) {
		Queue<EnergyTimestep> timesteps = new LinkedBlockingQueue<EnergyTimestep>();
		
		Calendar startDate = dateUtils.getPreviousFiveMinuteIncrement(firstMeasurementDate);
		Calendar endDate = dateUtils.getPreviousFiveMinuteIncrement(lastMeasurementDate);
		
		logger.debug("startDate: " + startDate + ", endDate: " + endDate);
		
		while(startDate.getTimeInMillis() < endDate.getTimeInMillis()) {
			EnergyTimestep timestep = new EnergyTimestep();
			
			timestep.setStartTime(startDate.getTime());
			
			startDate.add(Calendar.MINUTE, 5);
			startDate.add(Calendar.SECOND,-1);
			timestep.setEndTime(startDate.getTime());
			
			startDate.add(Calendar.SECOND,1);
			
			//logger.debug("Adding timestep " + timestep.getStartTime() + " - " + timestep.getEndTime());
			timesteps.add(timestep);
		}
		
		return timesteps;
	}
}

