(ns picture-gallery.validation
  (:require [struct.core :as st]))

(def registration-schema
  {:id [st/required]
   :pass [st/required st/min-count 7]})

