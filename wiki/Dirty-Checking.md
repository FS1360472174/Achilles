 Dirty check is performed only on *managed* entities and on all fields that are not of **Counter** type. It includes of course collections and map fields.
 
 Internally, **Achilles** intercepts all setter invocations upon mapped fields of *managed* entities and flag them as dirty. At flush time , when calling `merge()`, all dirty fields are persisted in **Cassandra**. This is done by using mutator in **Thrift** and atomic batch in **CQL3** (with **Achilles** 3.0 to come). All the changes will be sent at once to **Cassandra**. 

> **As a consequence of this design, internal calls inside an entity cannot be intercepted and will escape dirty check mechanism. It is thus recommended to change state of the entities using accessors**
 
 However, since there is a default limit of *15Mb* in a single message payload ( _thrift\_transport\_size\_in\_mb_ property in **cassandra.yaml**) for **Thrift** implementation, we strongly recommend not to have too many elements in dirty collections/maps or too big POJO as field.

 For **CQL**, there is no _known_ limitation at the moment (to be precise there is no documentation on this limitation right now, maybe in the native protocol specs). 
 
 **Achilles** extends collections and maps dirty check support to the _iterator, entrySet, entryIterator, keySet, listIterator_ and _valueCollections_ returned by the persistence manager. Every time you get any of the above type on a managed entity, **Achilles** will create an appropriate wrapper to track dirtiness.
 
 Below is a matrix of types and operations supported by dirty check:
 
<table border=0 cellpadding=0 cellspacing=0 width=720 style='border-collapse:
 collapse;table-layout:fixed;width:540pt'>
 <col width=80 span=9 style='width:60pt'>
 <tr height=40 style='height:30.0pt'>
  <td height=40 width=80 style='height:30.0pt;width:60pt'></td>
  <td width=80 style='width:60pt'><strong>Collection</strong></td>
  <td width=80 style='width:60pt'><strong>List</strong></td>
  <td width=80 style='width:60pt'><strong>Set</strong></td>
  <td width=80 style='width:60pt'><strong>Map</strong></td>
  <td class=xl66 width=80 style='width:60pt'><strong>Collection.<br>iterator</strong></td>
  <td class=xl66 width=80 style='width:60pt'><strong>List.<br>iterator</strong></td>
  <td class=xl66 width=80 style='width:60pt'><strong>List.<br>listIterator</strong></td>
  <td class=xl66 width=80 style='width:60pt'><strong>Set.<br>iterator</strong></td>
 </tr>
 <tr height=20 style='height:15.0pt'>
  <td height=20 class=xl65 style='height:15.0pt'><strong>add()</strong></td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>YES</td>
  <td class=xl65>N/A</td>
 </tr>
 <tr height=20 style='height:15.0pt'>
  <td height=20 class=xl65 style='height:15.0pt'><strong>addAll()</strong></td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
 </tr>
 <tr height=20 style='height:15.0pt'>
  <td height=20 class=xl65 style='height:15.0pt'><strong>clear()</strong></td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
 </tr>
 <tr height=20 style='height:15.0pt'>
  <td height=20 class=xl65 style='height:15.0pt'><strong>remove()</strong></td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
 </tr>
 <tr height=20 style='height:15.0pt'>
  <td height=20 class=xl65 style='height:15.0pt'><strong>removeAll()</strong></td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
 </tr>
 <tr height=20 style='height:15.0pt'>
  <td height=20 class=xl65 style='height:15.0pt'><strong>retainAll()</strong></td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
 </tr>
 <tr height=20 style='height:15.0pt'>
  <td height=20 class=xl65 style='height:15.0pt'><strong>add(pos,value)</strong></td>
  <td class=xl65>N/A</td>
  <td class=xl65>YES</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
 </tr>
 <tr height=20 style='height:15.0pt'>
  <td height=20 class=xl65 style='height:15.0pt'><strong>addAll(pos,col)</strong></td>
  <td class=xl65>N/A</td>
  <td class=xl65>YES</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
 </tr>
 <tr height=20 style='height:15.0pt'>
  <td height=20 class=xl65 style='height:15.0pt'><strong>set(pos,val)</strong></td>
  <td class=xl65>N/A</td>
  <td class=xl65>YES</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
 </tr>
 <tr height=20 style='height:15.0pt'>
  <td height=20 class=xl65 style='height:15.0pt'><strong>put(key,val)</strong></td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>YES</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
 </tr>
 <tr height=20 style='height:15.0pt'>
  <td height=20 class=xl65 style='height:15.0pt'><strong>putAll(map)</strong></td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>YES</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
 </tr>
 <tr height=20 style='height:15.0pt'>
  <td height=20 class=xl65 style='height:15.0pt'><strong>set(value)</strong></td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>N/A</td>
  <td class=xl65>YES</td>
  <td class=xl65>N/A</td>
 </tr>
</table>

<br/>

<table border=0 cellpadding=0 cellspacing=0 width=776 style='border-collapse:
 collapse;table-layout:fixed;width:582pt'>
 <col width=82 style='mso-width-source:userset;mso-width-alt:2998;width:62pt'>
 <col width=132 style='mso-width-source:userset;mso-width-alt:4827;width:99pt'>
 <col width=99 style='mso-width-source:userset;mso-width-alt:3620;width:74pt'>
 <col width=132 style='mso-width-source:userset;mso-width-alt:4827;width:99pt'>
 <col width=80 style='width:60pt'>
 <col width=132 style='mso-width-source:userset;mso-width-alt:4827;width:99pt'>
 <col width=119 style='mso-width-source:userset;mso-width-alt:4352;width:89pt'>
 <tr height=60 style='height:45.0pt'>
  <td height=60 width=82 style='height:45.0pt;width:62pt'></td>
  <td class=xl66 width=132 style='width:99pt'><strong>Map.<br>entrySet</strong></td>
  <td class=xl66 width=99 style='width:74pt'><strong>Map.<br>entrySet.<br>iterator</strong></td>
  <td class=xl66 width=132 style='width:99pt'><strong>Map.<br>keySet</strong></td>
  <td class=xl66 width=80 style='width:60pt'><strong>Map.<br>keySet.<br>iterator</strong></td>
  <td class=xl66 width=132 style='width:99pt'><strong>Map.<br>valuesCollection</strong></td>
  <td class=xl66 width=119 style='width:89pt'><strong>Map.<br>valuesCollection.<br>iterator</strong></td>
 </tr>
 <tr height=40 style='height:30.0pt'>
  <td height=40 class=xl65 style='height:30.0pt'><strong>add()</strong></td>
  <td class=xl66 width=132 style='width:99pt'><strong>Unsupported<br>OperationException</strong></td>
  <td class=xl65>N/A</td>
  <td class=xl66 width=132 style='width:99pt'><strong>Unsupported<br>OperationException</strong></td>
  <td class=xl65>N/A</td>
  <td class=xl66 width=132 style='width:99pt'><strong>Unsupported<br>OperationException</strong></td>
  <td class=xl65>N/A</td>
 </tr>
 <tr height=40 style='height:30.0pt'>
  <td height=40 class=xl65 style='height:30.0pt'><strong>addAll()</strong></td>
  <td class=xl66 width=132 style='width:99pt'><strong>Unsupported<br>OperationException</strong></td>
  <td class=xl65>N/A</td>
  <td class=xl66 width=132 style='width:99pt'><strong>Unsupported<br>OperationException</strong></td>
  <td class=xl65>N/A</td>
  <td class=xl66 width=132 style='width:99pt'><strong>Unsupported<br>OperationException</strong></td>
  <td class=xl65>N/A</td>
 </tr>
 <tr height=20 style='height:15.0pt'>
  <td height=20 class=xl65 style='height:15.0pt'><strong>clear()</strong></td>
  <td class=xl65>YES</td>
  <td class=xl65>N/A</td>
  <td class=xl65>YES</td>
  <td class=xl65>N/A</td>
  <td class=xl65>YES</td>
  <td class=xl65>N/A</td>
 </tr>
 <tr height=20 style='height:15.0pt'>
  <td height=20 class=xl65 style='height:15.0pt'><strong>remove()</strong></td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
  <td class=xl65>YES</td>
 </tr>
 <tr height=20 style='height:15.0pt'>
  <td height=20 class=xl65 style='height:15.0pt'><strong>removeAll()</strong></td>
  <td class=xl65>YES</td>
  <td class=xl65>N/A</td>
  <td class=xl65>YES</td>
  <td class=xl65>N/A</td>
  <td class=xl65>YES</td>
  <td class=xl65>N/A</td>
 </tr>
 <tr height=20 style='height:15.0pt'>
  <td height=20 class=xl65 style='height:15.0pt'><strong>retainAll()</strong></td>
  <td class=xl65>YES</td>
  <td class=xl65>N/A</td>
  <td class=xl65>YES</td>
  <td class=xl65>N/A</td>
  <td class=xl65>YES</td>
  <td class=xl65>N/A</td>
 </tr>
</table>

<br/>
>	Please note that when a collection or map is marked as dirty, at flush time **Achilles** will remove all existing value and save all the current value to **Cassandra**. **There is no *atomic* persist or removal operation for dirty elements in a collection or map**.

<br/>
	
 Indeed it is technically possible to only flush elements of the collection/map marked as dirty but requires so many work that it does not worth it and the gain in bandwidth is not significant, especially when we consider that collections or maps contains less than 1000 elements ad recommended above.
 
  Example with **ListIterator**:
```java
// Favorite tags list initially contains {"computing","java","cassandra"}
ListIterator<String> iter = user.getFavoriteTags().listIterator();

iter.next();

// entire favorite tags list is marked as dirty
iter.set("achilles");
	
// Remove all existing favorite tags and persist the new ones
manager.merge(user);
```
