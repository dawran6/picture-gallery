(ns picture-gallery.routes.services.auth
  (:require [buddy.hashers :as hashers]
            [clojure.tools.logging :as log]
            [picture-gallery.db.core :as db]
            [ring.util.http-response :as response]
            [picture-gallery.validation :as validation]))

(defn handle-registration-error [e]
  (if (and
       (instance? java.sql.SQLException e)
       (-> e
           (.getNextException)
           (.getMessage)
           (.startsWith "ERROR: duplicate key value")))
    (response/precondition-failed
     {:result :error
      :message "user with the selected ID already exists"})
    (do
      (log/error e)
      (response/internal-server-error
       {:result :error
        :message "server error occurred while adding the user"}))))

(defn register!
  [{:keys [session]} user]
  (if-let [error (validation/registration-errors user)]
    (response/precondition-failed {:result :error
                                   :message error})
    (try
      (db/create-user!
       (-> user
           (dissoc :pass-confirm)
           (update :pass hashers/encrypt)))
      (-> {:result :ok}
          (response/ok)
          (assoc :session (assoc session :identity (:id user))))
      (catch Exception e
        (handle-registration-error e)))))
