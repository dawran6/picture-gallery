(ns picture-gallery.components.login
  (:require [reagent.core :as r]
            [goog.crypt.base64 :as base64]
            [clojure.string :as string]
            [ajax.core :as ajax]
            [picture-gallery.components.common :as c]
            [picture-gallery.core :as core :refer [session]]))

(defn encode-auth [user pass]
  (->> (str user ":" pass)
       (base64/encodeString)
       (str "Basic ")))

(defn login! [fields error]
  (let [{:keys [id pass]} @fields]
    (reset! error nil)
    (ajax/POST "/api/login"
               {:headers {"Authorization" (encode-auth (string/trim id) pass)}
                :handler #(do
                            (swap! session dissoc :modal)
                            (swap! session assoc :identity id)
                            (reset! fields nil))
                :error-handler #(reset! error (get-in % [:response :message]))})))

(defn login-form []
  (let [fields (r/atom {})
        error (r/atom nil)]
    (fn []
      [c/modal
       [:div "Picture Gallery Login"]
       [:div
        [:div
         [:strong "* required field"]]
        [c/text-input "name" :id "enter a user name" fields]
        [c/password-input "password" :pass "enter a password" fields]
        (when-let [error @error]
          [:div error])]
       [:div
        [:button.button
         {:on-click #(login! fields error)}
         "Login"]
        [:button.button
         {:on-click #(swap! session dissoc :modal)}
         "Cancel"]]])))
