package se.treehou.ng.ohcommunicator.services.callbacks;

public class OHResponse<G> {

    private G body;
    private boolean fromCache;

    public static class Builder<G> {

        private G body;
        private boolean fromCache = false;

        /**
         * A response builder.
         * @param body the response object.
         */
        public Builder(G body) {
            this.body = body;
        }

        /**
         * Set if response comes from cache or not.
         * @param fromCache true if response is from cache. Else false.
         * @return this builder.
         */
        public Builder<G> fromCache(boolean fromCache){
            this.fromCache = fromCache;
            return this;
        }

        /**
         * Builds the response object
         * @return response object with values from builder.
         */
        public OHResponse<G> build(){
            return new OHResponse<>(body, fromCache);
        }
    }

    private OHResponse(G body, boolean fromCache) {
        this.body = body;
        this.fromCache = fromCache;
    }

    /**
     * Get the response data received from the server
     *
     * @return the response data.
     */
    public G body(){
        return body;
    }

    /**
     * If the response comes from internal cache.
     *
     * @return true if response is from cache, else false.
     */
    public boolean fromCache(){
        return fromCache;
    }
}
