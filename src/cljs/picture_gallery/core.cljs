(ns picture-gallery.core
  (:require [ajax.core :refer [GET POST]]
            [clojure.string :as string]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [picture-gallery.ajax :as ajax]
            [picture-gallery.components.common :as c]
            [picture-gallery.components.login :as login]
            [picture-gallery.components.registration :as reg]
            [reagent.core :as r]
            [reitit.core :as reitit])
  (:import goog.History))

(defonce session (r/atom {:page :home}))

(defn user-menu []
  (if-let [id (:identity @session)]
    [:a.navbar-item
     {:on-click #(POST
                  "/api/logout"
                  {:handler (fn [] (swap! session dissoc :identity))})}
     [:i.fa.fa-user] " " id " | sign out"]
    [:<>
     [:a.navbar-item
      {:on-click #(swap! session assoc :modal login/login-form)}
      "login"]
     [:a.navbar-item
      {:on-click #(swap! session assoc :modal reg/registration-form)}
      "register"]]))

(defn nav-link [uri title page]
  [:a.navbar-item
   {:href   uri
    :active (when (= page (:page @session)) "is-active")}
   title])

(defn navbar []
  (r/with-let [expanded? (r/atom false)]
    [:nav.navbar.is-info>div.container
     [:div.navbar-brand
      [:a.navbar-item {:href "/" :style {:font-weight :bold}} "picture-gallery"]
      [:span.navbar-burger.burger
       {:data-target :nav-menu
        :on-click #(swap! expanded? not)
        :class (when @expanded? :is-active)}
       [:span][:span][:span]]]
     [:div#nav-menu.navbar-menu
      {:class (when @expanded? :is-active)}
      [:div.navbar-start
       [nav-link "#/" "Home" :home]
       [nav-link "#/about" "About" :about]]
      [:div.navbar-end
       [user-menu]]]]))

(defn modal []
  (when-let [session-modal (:modal @session)]
    [session-modal session]))

(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]])

(defn home-page []
  [:section.section>div.container>div.content
   (when-let [docs (:docs @session)]
     [:div {:dangerouslySetInnerHTML {:__html (md->html docs)}}])])

(def pages
  {:home #'home-page
   :about #'about-page})

(defn page []
  [:div
   [modal]
   [(pages (:page @session))]])

;; -------------------------
;; Routes
(def router
  (reitit/router
   [["/" :home]
    ["/about" :about]]))

(defn match-route [uri]
  (->> (or (not-empty (string/replace uri #"^.*#" "")) "/")
       (reitit/match-by-path router)
       :data
       :name))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     HistoryEventType/NAVIGATE
     (fn [event]
       (swap! session assoc :page (match-route (.-token event)))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs" {:handler #(swap! session assoc :docs %)}))

(defn mount-components []
  (r/render [#'navbar] (.getElementById js/document "navbar"))
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (ajax/load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
