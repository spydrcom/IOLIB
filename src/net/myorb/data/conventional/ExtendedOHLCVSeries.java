
package net.myorb.data.conventional;

import net.myorb.gui.graphics.markets.data.MarketQuoteParameters.Periods;

/**
 * time frame annotation formats
 * @author Michael Druckman
 */
public class ExtendedOHLCVSeries  extends OHLCV.Series
{

	public ExtendedOHLCVSeries (Periods selectedPeriod)
	{ this.selectedPeriod = selectedPeriod; }
	Periods selectedPeriod;

	/* (non-Javadoc)
	 * @see net.myorb.data.conventional.OHLCV.Series#annotate(net.myorb.data.conventional.OHLCV.Bar, net.myorb.data.conventional.CharacterDelimited.Reader)
	 */
	public void annotate (OHLCV.Bar bar, CharacterDelimited.Reader fromReader)
	{
		String date = fromReader.getText ("Date"), time = fromReader.getText ("Time");
		String [] hms = time.split (":"), ymd = date.split ("-");
		String md = ymd[1] + "/" + ymd[2];

		switch (selectedPeriod)
		{
			case HOUR:	bar.setDate (md + "H" + hms[0]); break;
			case MIN:	bar.setDate (md + "H" + hms[0] + ":" + hms[1]); break;
			default:	break;
		}

		String accum;
		if ( ( accum = fromReader.getText ("Accum") ) != null)
		{ bar.associateStudyValue ("Flow", Double.parseDouble (accum)); }
	}

}
