<script>
    module.exports = {
        props: ['value'],
        render() {
            if (this.$slots.default && this.$slots.default.length <= 1) {
                if (!this.$slots.default[0].data) {
                    this.$slots.default[0].data = {}
                }
                let data = this.$slots.default[0].data;
                if (!data.staticClass) {
                    data.staticClass = "";
                }
                if (!data.on) {
                    data.on = {};
                }
                data.staticClass += ' documented';
                data.on.mouseenter = () => this.$documentation.display(this.value, this.$el);
                return this.$slots.default[0];
            } else {
                throw new Error('documentation.vue can only render one, and exactly one child component.');
            }
            return null;
        }
    };
</script>