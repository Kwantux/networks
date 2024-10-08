package de.kwantux.networks.storage;

public class NetworkProperties {
        int baseRange;
        int maxComponents;
        int maxUsers;

        public int baseRange() {
            return baseRange;
        }
        public int maxComponents() {
            return maxComponents;
        }
        public int maxUsers() {
            return maxUsers;
        }

        public void baseRange(int baseRange) {
            this.baseRange = baseRange;
        }
        public void maxComponents(int maxComponents) {
            this.maxComponents = maxComponents;
        }
        public void maxUsers(int maxUsers) {
            this.maxUsers = maxUsers;
        }

        public NetworkProperties(int baseRange, int maxComponents, int maxUsers) {
            this.baseRange = baseRange;
            this.maxComponents = maxComponents;
            this.maxUsers = maxUsers;
        }
}
