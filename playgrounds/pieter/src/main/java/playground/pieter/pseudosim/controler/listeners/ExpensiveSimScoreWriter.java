package playground.pieter.pseudosim.controler.listeners;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.matsim.core.controler.events.IterationEndsEvent;
import org.matsim.core.controler.events.ShutdownEvent;
import org.matsim.core.controler.events.StartupEvent;
import org.matsim.core.controler.listener.IterationEndsListener;
import org.matsim.core.controler.listener.ShutdownListener;
import org.matsim.core.controler.listener.StartupListener;
import org.matsim.core.utils.charts.XYLineChart;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.io.UncheckedIOException;

import playground.pieter.pseudosim.controler.PseudoSimControler;

public class ExpensiveSimScoreWriter implements IterationEndsListener,
		ShutdownListener, StartupListener {
	PseudoSimControler controler;
	private BufferedWriter out;
	final private static int INDEX_WORST = 0;
	final private static int INDEX_BEST = 1;
	final private static int INDEX_AVERAGE = 2;
	final private static int INDEX_EXECUTED = 3;

	public ExpensiveSimScoreWriter(PseudoSimControler controler) {
		super();
		this.controler = controler;

	}

	@Override
	public void notifyIterationEnds(IterationEndsEvent event) {
		ArrayList<Integer> expensiveIters = MobSimSwitcher.getExpensiveIters();
		int index = expensiveIters.size();
		if (!MobSimSwitcher.isMobSimIteration || event.getIteration()==controler.getLastIteration()) {
			return;
		}
		double[][] history = controler.getScoreStats().getHistory();
		int idx = event.getIteration() - controler.getFirstIteration();
		try {
			out.write(event.getIteration() + "\t"
					+ history[INDEX_EXECUTED][idx] + "\t"
					+ history[INDEX_WORST][idx] + "\t"
					+ history[INDEX_AVERAGE][idx] + "\t"
					+ history[INDEX_BEST][idx] + "\n");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// create chart when data of more than one iteration is available.
		if(index<2){
			return;
		}
		XYLineChart chart = new XYLineChart("QSIM Score Statistics",
				"iteration", "score");
		double[] iterations = new double[index];
		for (int i = 0; i < index; i++) {
			iterations[i] = i + controler.getFirstIteration();
		}
		double[] values = new double[index];
		double[] fullhist = new double[event.getIteration()
				- controler.getFirstIteration() + 1];
		int[] series = { INDEX_WORST, INDEX_BEST, INDEX_AVERAGE, INDEX_EXECUTED };
		String[] seriesNames = { "avg. worst score", "avg. best score",
				"avg. of plans' average score", "avg. executed score" };
		for (int s = 0; s < series.length; s++) {
			System.arraycopy(history[series[s]], 0, fullhist, 0,
					fullhist.length);
			int valuecounter = 0;
			for (int i : expensiveIters) {
				values[valuecounter++] = fullhist[i
						- controler.getFirstIteration()];
			}
			chart.addSeries(seriesNames[s], iterations, values);

		}




		chart.addMatsimLogo();
		chart.saveAsPng(controler.getControlerIO().getOutputPath()
				+ "/qsimstats.png", 1200, 800);

	}

	@Override
	public void notifyShutdown(final ShutdownEvent controlerShudownEvent) {
		try {
			this.out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void notifyStartup(StartupEvent event) {
		String fileName = controler.getControlerIO().getOutputPath()
				+ "/qsimstats.txt";
		this.out = IOUtils.getBufferedWriter(fileName);

		try {
			this.out.write("ITERATION\tavg. EXECUTED\tavg. WORST\tavg. AVG\tavg. BEST\n");
			this.out.flush();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

	}
}
