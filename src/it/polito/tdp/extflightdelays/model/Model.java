package it.polito.tdp.extflightdelays.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	private Map<Integer, Airport> idMap;
	private SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	
	public Model() {
		idMap= new HashMap<>();
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
	}

	public boolean isDigit(String numAirlain) {
		if(numAirlain.matches("\\d+")) {
			return true;
		}
		return false;
	}

	public String creaGrafo(String numAirlain) {
		ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();
		String risultato="";
		dao.loadAllAirports(idMap);
		List<Rotte> rotte = dao.getRotte(idMap, numAirlain);
		for(Rotte r: rotte) {
			if(!grafo.containsVertex(r.getA1())) {
				grafo.addVertex(r.getA1());
			}
			if(!grafo.containsVertex(r.getA2())) {
				grafo.addVertex(r.getA2());
			}
			DefaultWeightedEdge edge = grafo.getEdge(r.getA1(), r.getA2());
			if(edge==null) {
				Graphs.addEdgeWithVertices(grafo, r.getA1(), r.getA2(), r.getPeso());
			}else {
				grafo.setEdgeWeight(edge, r.getPeso());
			}
		}
		risultato+="Grafo creato! Vertici: "+grafo.vertexSet().size()+" Archi: "+grafo.edgeSet().size()+"\n";
		return risultato;
	}

	public List<Airport> getVertici() {
		List<Airport> vertici = new LinkedList<>(grafo.vertexSet());
		return vertici;
	}

	public String getConnessioni(Airport partenza) {
		String risultato="";
		ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();
		List<Airport> vicini = Graphs.neighborListOf(grafo, partenza);
		Collections.sort(vicini, new Comparator<Airport>() {

			@Override
			public int compare(Airport a1, Airport a2) {
				DefaultWeightedEdge edge1 = grafo.getEdge(a1, partenza);
				double peso1 = grafo.getEdgeWeight(edge1);
				DefaultWeightedEdge edge2 = grafo.getEdge(a2, partenza);
				double peso2 = grafo.getEdgeWeight(edge2);
				return (int) (peso2-peso1);
			}
		});
		
		for(Airport a: vicini) {
			DefaultWeightedEdge edge = grafo.getEdge(partenza, a);
			risultato+=a.getAirportName()+" "+grafo.getEdgeWeight(edge)+"\n";
		}
		return risultato;
	}

}
