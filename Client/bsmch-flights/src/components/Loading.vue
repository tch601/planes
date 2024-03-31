<template>
    <v-dialog v-model="loading" hide-overlay persistent width="375">
        <v-card color="#424242" dark height="200">
            <v-card-text style="text-align:center">
                עוד רגע ונמשיך...
                <awesome-loaders />
            </v-card-text>
        </v-card>
    </v-dialog>
</template>

<script>
    import eventbus from '@/plugins/eventbus'

    export default {
        created() {
            eventbus.$on('startloading', () => {
                this.loading = true; 
               
            });
             eventbus.$on('stoploading', () => {
                this.loading = false; 
            });
        },
        data () {
            return {
                loading: false
            }
        },
    }
</script>