(ns defng.clj-dom.core
  (:import (org.jdom2.xpath XPathFactory)
           (org.jdom2.filter Filters)
           (org.jdom2.input SAXBuilder)
           (java.io StringReader)))

(defn parse-str->Document
  "Parse xml string into JDOM Document"
  [xml]
  (let [builder (SAXBuilder.)
        reader  (StringReader. xml)]
    (.build builder reader)))

(defn parse-str
  "Parse xml string into JDOM Document and return the root element"
  [xml]
  (let [document (parse-str->Document xml)]
    (.getRootElement document)))

(defn find-first
  "Return the first element at the XPath"
  ([root xpath]
   (let [xpath-expr (.compile (XPathFactory/instance) xpath (Filters/element))]
     (.evaluateFirst xpath-expr root)))
  ([root xpath namespaces]
   (let [xpath-expr (.compile (XPathFactory/instance) xpath (Filters/element) nil namespaces)]
     (.evaluateFirst xpath-expr root))))

(defn find-all
  "Return collection of elements at the XPath"
  ([root xpath]
   (let [xpath-expr (.compile (XPathFactory/instance) xpath (Filters/element))]
     (.evaluate xpath-expr root)))
  ([root xpath namespaces]
   (let [xpath-expr (-> (XPathFactory/instance)
                        (.compile xpath (Filters/element) nil namespaces))]
     (.evaluate xpath-expr root))))

(defn get-value
  "Return the value at the XPath as a String, regardless of whether the XPath points to a element or attribute"
  ([root xpath]
   (let [paths (clojure.string/split xpath #"/")
         element-or-attr (last paths)]
     (if (clojure.string/starts-with? element-or-attr "@")
       (let [element-path (clojure.string/join "/" (butlast paths))
             element (find-first root element-path)]
         (.getAttributeValue element (subs element-or-attr 1)))
       (let [element (find-first root xpath)]
         (.getValue element)))))
  ([root xpath namespaces]
   (let [paths (clojure.string/split xpath #"/")
         element-or-attr (last paths)]
     (if (clojure.string/starts-with? element-or-attr "@")
       (let [element-path (clojure.string/join "/" (butlast paths))
             element (find-first root element-path namespaces)]
         (.getAttributeValue element (subs element-or-attr 1)))
       (let [element (find-first root xpath namespaces)]
         (.getValue element))))))
