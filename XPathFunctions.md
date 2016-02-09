# XPath Functions #

## Retrieving a topic by its subject identifier ##

The function `sid` expects a string and returns a topic with a subject identifier equals to the the specified string.

Example:

```
// Find the topic "John_Lennon" and return all roles it plays

IFilter<Role> filter = XPathFilter.create("sid('http://de.wikipedia.org/wiki/John_Lennon')/role");

for (Role role: filter.match(topicMap)) {
   // ...
}
```


## Retrieving a topic by its subject locator ##

The function `slo` expects a string and returns a topic with a subject locator equals to the the specified string.

Example:

```
// Find a topic with the subject locator "http://www.semagia.com/" and return all occurrences

IFilter<Occurrence> filter = XPathFilter.create("slo('http://www.semagia.com/')/occurrence");

for (Occurrence occ: filter.match(topicMap)) {
   // ...
}
```



## Retrieving a Topic Maps construct by its item identifier ##

The function `iid` expects a string and returns a `org.tmapi.core.Construct` with an item identifier equals to the the specified string.

Example:

```
// Find a construct with the item identifier "http://www.semagia.com/map#id" and return the parent

IFilter<Construct> filter = XPathFilter.create("iid('http://www.semagia.com/map#id')/..");

Construct parent = filter.matchOne(topicMap);
```


## Check if a statement is in the unconstrained scope ##

The function `in-ucs` expects a `org.tmapi.core.Scoped` instance and returns `true` if the statement is in the unconstrained scope, otherwise `false`.

Example:
```
// Find those names which are in the unconstrained constrained scope
IFilter<Name> filter = XPathFilter.create("name[in-ucs(.)]"); 

for (Name name: filter.match(topic)) {
// ...
}
```

In fact, the `in-ucs` function is equivalent to the following code, but requires less typing, is faster, and should be more clear:
```
// Find those names which are in the unconstrained constrained scope
IFilter<Name> filter = XPathFilter.create("name[count(./scope) = 0]"); 

for (Name name: filter.match(topic)) {
// ...
}
```


## Check if a name uses the default name type ##

The function `default-name` expects a `org.tmapi.core.Name` instance and returns `true` if the name has the default name type (`http://psi.topicmaps.org/iso13250/model/topic-name`), otherwise `false`.

Example:
```
// Returns all string values of all names which use the default name type.
IFilter<String> filter = XPathFilter.create("name[default-name(.)]/value"); 

String defaultName = filter.matchOne(topic);
```