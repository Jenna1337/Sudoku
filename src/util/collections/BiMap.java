package util.collections;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BiMap<K, V> extends AbstractMap<K, V>
{
	private HashMap<V, K> valueMap = new HashMap<>();
	private HashMap<K, V> keyMap = new HashMap<>();
	
	public V put(K k, V v)
	{
		valueMap.put(v, k);
		return keyMap.put(k, v);
	}
	public K putValue(V v, K k)
	{
		keyMap.put(k, v);
		return valueMap.put(v, k);
	}
	/**
	 * Returns the key to which the specified value is mapped, or null if this map contains no mapping for the value. <br/>
	 * <br/>
	 * More formally, if this map contains a mapping from a value v to a key k such that (value==null ? v==null : value.equals(v)), then this method returns k; otherwise it returns null. (There can be at most one such mapping.) <br/>
	 * <br/>
	 * A return key of null does not necessarily indicate that the map contains no mapping for the value; it's also possible that the map explicitly maps the value to null. The containsvalue operation may be used to distinguish these two cases.<br/>
	 * 
	 * @param k the value whose associated key is to be returnedReturns:the key to which the specified value is mapped, or null if this map contains no mapping for the value
	 */
	public K getKey(V v){
		return valueMap.get(v);
	}
	public boolean containsValue(Object value){
		return valueMap.containsKey(value);
	}
	public V remove(Object key)
	{
		V val = keyMap.remove(key);
		valueMap.remove(val);
		return val;
	}
	/**
	 *Removes the mapping for the specified value from this map if present.
	 *
	 *@param val whose mapping is to be removed from the map
	 *@returns the previous key associated with value, or null if there was no mapping for value. (A null return can also indicate that the map previously associated null with value.)
	 */
	public K removeValue(Object val)
	{
		K key = valueMap.remove(val);
		keyMap.remove(key);
		return key;
	}
	public void putAll(Map<? extends K, ? extends V> m){
		for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
			put(e.getKey(), e.getValue());
	}
	/**
	 * Copies all of the mappings from the specified map to this map. These mappings will replace any mappings that this map had for any of the keys currently in the specified map.
	 * 
	 * @param m mappings to be stored in this map
	 */
	public void putAllValues(Map<? extends V, ? extends K> m){
		for (Map.Entry<? extends V, ? extends K> e : m.entrySet())
			put(e.getValue(), e.getKey());
	}
	public void clear()
	{
		this.clear();
		valueMap.clear();
	}
	public Set<Entry<K, V>> entrySet(){
		return keyMap.entrySet();
	}
}
