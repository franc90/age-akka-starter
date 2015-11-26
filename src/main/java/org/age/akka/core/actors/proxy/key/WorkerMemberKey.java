package org.age.akka.core.actors.proxy.key;

public class WorkerMemberKey implements MemberKey {

    private final String path;

    public WorkerMemberKey(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkerMemberKey that = (WorkerMemberKey) o;

        return path.equals(that.path);

    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public String toString() {
        return "WorkerMemberKey{" +
                "path='" + path + '\'' +
                '}';
    }

    public static class Builder {

        private String path;

        public Builder withPath(String path) {
            this.path = path;
            return this;
        }

        public WorkerMemberKey build() {
            return new WorkerMemberKey(path);
        }
    }
}
