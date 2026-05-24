package de.kwantux.networks.storage;

public class NetworkProperties {
        int baseRange;

        public int baseRange() {
            return baseRange;
        }

        public void baseRange(int baseRange) {
            this.baseRange = baseRange;
        }

        public NetworkProperties(int baseRange) {
            this.baseRange = baseRange;
        }
}
