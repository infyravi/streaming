/*
 * Here outputstream is the spring boot outputstream
 */
public void readfromsolrAndWriteToOutputStream(final OutputStream outputStream) {
		
		StreamFactory factory = new StreamFactory().withCollectionZkHost(COLLECTION_NAME,ZK_HOST);
		SolrClientCache solrClientCache = new SolrClientCache(httpClient);

		StreamContext streamContext = new StreamContext();
		streamContext.setSolrClientCache(solrClientCache);

		String expressionStr = String.format(SEARCH_EXPRESSION,COLLECTION_NAME);
		StreamExpression expression = StreamExpressionParser.parse(expressionStr);
		TupleStream stream;
		try {
			stream = new CloudSolrStream(expression, factory);
			stream.setStreamContext(streamContext);
			stream.open();
			Tuple tuple = stream.read();
			int count = 0;
			while (!tuple.EOF) {
				String jsonStr = ++count + " " + tuple.jsonStr() + "\r\n";
				outputStream.write(jsonStr.getBytes());
				outputStream.flush();
				tuple = stream.read();
			}
			stream.close();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
