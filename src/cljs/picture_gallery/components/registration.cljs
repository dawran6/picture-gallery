(ns picture-gallery.components.registration
  (:require [ajax.core :as ajax]
            [picture-gallery.components.common :as c]
            [picture-gallery.validation :as validation]
            [reagent.core :as r]))

(defn register! [session fields errors]
  (reset! errors (validation/registration-errors @fields))
  (when-not @errors
    (ajax/POST "/api/register"
               {:params @fields
                :handler
                #(do
                   (swap! session update :identity (:id @fields))
                   (reset! fields {})
                   (swap! session dissoc :modal))
                :error-handler
                #(reset! errors {:server-error (get-in % [:response :message])})})))


(defn registration-form [session]
  (let [fields (r/atom {})
        error  (r/atom nil)]
    (fn []
      [c/modal
       [:div "Picture Gallery Registration"]
       [:div
        [:strong "* required field"]
        [c/text-input "name" :id "enter a user name" fields]
        (when-let [error (first (:id @error))]
          [:div.help.is-danger error])
        [c/password-input "password" :pass "enter a password" fields]
        (when-let [error (first (:pass @error))]
          [:div.help.is-danger error])
        [c/password-input "password" :pass-confirm "re-enter the password" fields]
        (when-let [error (:server-error @error)]
          [:div.help.is-danger error])]
       [:div
        [:button.button.is-success
         {:on-click #(register! session fields error)}
         "Register"]
        [:button.button
         {:on-click #(swap! session dissoc :modal)}
         "Cancel"]]])))
