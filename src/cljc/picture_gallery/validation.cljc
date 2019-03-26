(ns picture-gallery.validation
  (:require [struct.core :as st]))

(def registration-schema
  {:id [st/required]
   :pass [st/required [st/min-count 7]]
   :pass-confirm [st/required [st/identical-to :pass]]})

(defn registration-errors [params]
  (first (st/validate params registration-schema)))
