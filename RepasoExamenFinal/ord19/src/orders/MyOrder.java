package orders;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class MyOrder implements Order, Serializable {

	  /**
	 * 
	 */
	private static final long serialVersionUID = 5655693553015814046L;

	public static void main(String[] args) {
	    MyOrder of = new MyOrder();
	    Item a = new Item(1, "patatas", 1.0f);
	    of.addItem(a, 10);
	    of.removeItem(a, 5);
	    System.out.println(a.getId() + ": " + of.getItem(a));
	  }

	  private HashMap<Item, Integer> mapaI;

	  public MyOrder() {
	    mapaI = new HashMap<>();
	  }

	  public void addItem(Item item, int cantidad) {
	    if (!this.mapaI.containsKey(item)) {
	      this.mapaI.put(item, cantidad);
	    } else {
	      Integer cantidadExistente = this.mapaI.get(item);
	      this.mapaI.put(item, cantidadExistente + cantidad);
	    }
	  }

	  public void removeItem(Item item, int cantidad) {
	    if (mapaI.containsKey(item)) {
	      Integer cantidadExistente = this.mapaI.get(item);
	      int cantidadRestante = cantidadExistente - cantidad;
	      if (cantidadRestante > 0) {
	        mapaI.put(item, cantidadRestante);
	      } else {
	        mapaI.remove(item);
	      }
	    }
	  }

	  public void removeAll() {
	    mapaI.clear();
	  }

	  public Integer getItem(Item item) {
	    return mapaI.get(item);
	  }

	  public float getTotalTime() {
	    float result = 0;

	    for (Entry<Item, Integer> e : mapaI.entrySet()) {
	      result += (e.getKey().getTimePerUnit() * e.getValue());
	    }
	    return result;
	  }

	  public int getDistinctItems() {
	    return mapaI.size();
	  }

	  public List<Entry<Item, Integer>> getSortedList() {
	    List<Entry<Item, Integer>> result = new ArrayList<Entry<Item, Integer>>(mapaI.entrySet());
	    Comparator<Entry<Item, Integer>> comparador = new Comparator<Entry<Item, Integer>>() {

	      @Override
	      public int compare(Entry<Item, Integer> o1, Entry<Item, Integer> o2) {
	        if (o1.getValue() == o2.getValue()) {
	          return o1.getKey().getId().compareTo(o2.getKey().getId());
	        }
	        return o2.getValue().compareTo(o1.getValue());
	      }
	    };

	    result.sort(comparador);

	    return result;
	  }
	  
	  @Override
	public String toString() {
		// Devuelve un String con toda la informacion de todos los items del mapa
		String items = "";
		for (Entry<Item, Integer> e : mapaI.entrySet()) {
			// .concat para concatenar
			items = items.concat("Item: " + e.getKey() + " Unidades: " + e.getValue());
		}
		return items;
	}

	}
