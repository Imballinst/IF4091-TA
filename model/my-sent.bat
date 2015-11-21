call apache-opennlp-1.6.0\bin\opennlp SentenceDetectorEvaluator -model sentenceDetector\id-sent.bin -data sentenceDetector\id-sent.eval -encoding UTF-8
timeout 3
apache-opennlp-1.6.0\bin\opennlp SentenceDetector sentenceDetector\id-sent.bin ^<sentenceDetector\test.txt^> sentenceDetector\test2.txt