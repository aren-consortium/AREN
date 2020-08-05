<template>
    <div class="scroll-content" id="aafImport">

        <div class="container">

            <div id="header">
                <h1>Import AAF</h1>
            </div>

            <div class="container">

                <div class="file-field input-field">
                    <div class="btn waves-effect waves-light">
                        <span>File</span>
                        <input type="file" id="file" ref="file" accept=".xml" @change="selectFile()">
                    </div>
                    <div class="file-path-wrapper">
                        <input class="file-path validate" type="text" v-bind:value="file.name">
                    </div>
                </div>

                <button v-bind:class="['right', 'btn', 'waves-effect', 'waves-light', {disabled: !file}]"
                        @click="startImport()">
                    {{ progress !== false ? "En cours..." : "Importer" }}
                    <i v-if="progress === false" class="material-icons right">send</i>
                </button>
                <div class="progress">
                    <div class="determinate" v-bind:style="'width: ' + progress + '%'"></div>
                </div>

            </div>
        </div>
    </div>
</template>

<script>
    module.exports = {
        data( ) {
            return {
                progress: false,
                file: false
            };
        },
        methods: {
            startImport( ) {
                this.progress = 0;
                let data = new FormData();
                data.append('file', this.file);
                this.file = false;
                ArenService.import({
                    data,
                    onSuccess: (response) => {
                        this.$confirm({
                            title: "OK",
                            message: "OK",
                            isInfo: true
                        });
                        this.progress = false;
                        this.file = false;
                        this.$refs.file.value = "";
                    },
                    onProgress: (response) => {
                        this.progress = response * 100;
                    },
                    loading: false
                });
            },
            selectFile( ) {
                this.file = this.$refs.file.files[0];
            }
        }
    };
</script>