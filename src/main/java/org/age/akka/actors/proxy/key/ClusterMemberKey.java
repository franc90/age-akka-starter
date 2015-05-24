package org.age.akka.actors.proxy.key;

public class ClusterMemberKey implements MemberKey {

    private final String path;
    private final int order;

    private ClusterMemberKey(String path, int order) {
        this.path = path;
        this.order = order;
    }

    @Override
    public String getPath() {
        return path;
    }

    public int getOrder() {
        return order;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClusterMemberKey that = (ClusterMemberKey) o;

        return path.equals(that.path);

    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public String toString() {
        return "ClusterMemberKey{" +
                "path='" + path + '\'' +
                '}';
    }

    public static class Builder {
        private String path;
        private int order;

        public Builder withPath(String path) {
            this.path = path;
            return this;
        }

        public Builder withOrder(int order) {
            this.order = order;
            return this;
        }

        public ClusterMemberKey build() {
            return new ClusterMemberKey(path, order);
        }
    }
}
