(ns picture-gallery.components.registration
  (:require [picture-gallery.components.common :as c]
            [reagent.core :as r]))

(defn registration-form []
  (let [fields (r/atom {})]
    (fn []
      [c/modal
       [:div "Picture Gallery Registration"]
       [:div
        [:strong "* required field"]
        [c/text-input "name" :id "enter a user name" fields]
        [c/password-input "password" :pass "enter a password" fields]
        [c/password-input "password" :pass-confirm "re-enter the password" fields]]
       [:div
        [:button.button.is-success "Register"]
        [:button.button "Cancel"]]])))
