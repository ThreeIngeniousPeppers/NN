/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core

 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information on Heaton Research copyrights, licenses
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.NN;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.commons.io.IOUtils;
import org.encog.ml.data.market.MarketDataType;
import org.encog.ml.data.market.TickerSymbol;
import org.encog.ml.data.market.loader.LoadedMarketData;
import org.encog.ml.data.market.loader.LoaderError;
import org.encog.ml.data.market.loader.MarketLoader;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;
import org.encog.util.http.FormUtility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;

/**
 * This class loads financial data from Yahoo. One caution on Yahoo data.
 *
 * @author jheaton
 */
public class CryptoCompareLoader implements MarketLoader {

    /**
     * This method builds a URL to load data from Yahoo Finance for a neural
     * network to train with.
     *
     * @param ticker
     *            The ticker symbol to access.
     * @param from
     *            The beginning date.
     * @param to
     *            The ending date.
     * @return The UEL
     * @throws IOException
     *             An error accessing the data.
     */
    private URL buildURL(final TickerSymbol ticker, final Date from,
                         final Date to) throws IOException {
        // construct the URL
        final OutputStream os = new ByteArrayOutputStream();
        final FormUtility form = new FormUtility(os, null);
        form.add("fsym", ticker.getSymbol().toUpperCase());
        form.add("tsym", "USD");
        form.add("limit", "" + (int)((to.getTime() - from.getTime())/(3600*24*1000)));
        form.add("toTs", "" + to.getTime()/1000);
        os.close();
        final String str = "https://min-api.cryptocompare.com/data/histoday?"
                + os.toString();
        return new URL(str);
    }

    /**
     * Load the specified financial data.
     *
     * @param ticker
     *            The ticker symbol to load.
     * @param dataNeeded
     *            The financial data needed.
     * @param from
     *            The beginning date to load data from.
     * @param to
     *            The ending date to load data to.
     * @return A collection of LoadedMarketData objects that represent the data
     *         loaded.
     */
    public Collection<LoadedMarketData> load(final TickerSymbol ticker,
                                             final Set<MarketDataType> dataNeeded, final Date from,
                                             final Date to) {

        try {
            final Collection<LoadedMarketData> result =
                    new ArrayList<LoadedMarketData>();
            final URL url = buildURL(ticker, from, to);

            final String jsonstr = IOUtils.toString(url, "UTF-8");


            ObjectMapper mapper = new ObjectMapper();
            // use the ObjectMapper to read the json string and create a tree
            JsonNode root = mapper.readTree(jsonstr);
            // lets find out what fields it has

            JsonNode Data = root.get("Data");
            Iterator<JsonNode> DataElements = Data.iterator();
            while (DataElements.hasNext()) {
                JsonNode index = DataElements.next();

                final Date date = new Date(index.get("time").asLong()*1000);
                final double adjClose = index.get("close").asDouble();
                final double open = index.get("open").asDouble();
                final double close = index.get("close").asDouble();
                final double high = index.get("high").asDouble();
                final double low = index.get("low").asDouble();
                final double volume = index.get("volumefrom").asDouble();

                final LoadedMarketData data =
                        new LoadedMarketData(date, ticker);
                data.setData(MarketDataType.ADJUSTED_CLOSE, adjClose);
                data.setData(MarketDataType.OPEN, open);
                data.setData(MarketDataType.CLOSE, close);
                data.setData(MarketDataType.HIGH, high);
                data.setData(MarketDataType.LOW, low);
                data.setData(MarketDataType.OPEN, open);
                data.setData(MarketDataType.VOLUME, volume);
                result.add(data);

                }
            return result;
        } catch (final IOException e) {
            throw new LoaderError(e);
        }
    }
}
