==============
TMAPIX Filters
==============

What is TMAPIX Filters?
-----------------------
The filter subpackage provides a simple mechanism to filter 
`TMAPI 2.0 <http://www.tmapi.org/2.0/>`_ constructs with minimal effort.
The ``IFilter`` interface is implementation independent. Currently, a
``IFilter`` implementation for `XPath 1.0 <http://www.w3.org/TR/xpath>`_ 
expressions is available, but more implementations (i.e. a Domain-specific 
language (DSL) or TMQL path expressions) will follow.


Installation
------------
First of all, you need the TMAPI 2.0 interfaces and a TMAPI 2.0 compatible Topic 
Maps processor. Further, you need the ``jaxen-1.1.1.jar`` and 
``semagia-tmapix-filter-<VERSION>.jar`` in your classpath.


XPath Usage
-----------
Assuming that ``topic`` is bound to a ``org.tmapi.core.Topic`` instance, you can 
receive all ``Association``s where the topic plays a role with the following 
XPath expression::

    IFilter<Association> filter = XPath.create("roles/..");
    for (Association assoc: filter.match(topic)) {
        doSomethingWith(assoc);
    }


Latest Version
--------------
Visit the TMAPIX homepage <http://tmapix.semagia.com/> for the latest version.


License
-------
TMAPIX is licensed under the Mozilla Public License Version (MPL) Version 1.1,
see LICENSE.txt for details.
